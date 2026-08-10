package com.example.lionproject2backend.lessonfile.service;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.repository.LessonRepository;
import com.example.lionproject2backend.lessonfile.config.LessonFileProperties;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileFailureCode;
import com.example.lionproject2backend.lessonfile.domain.LessonFileStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileDownloadUrlResponse;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileListResponse;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFileConfirmResponse;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFilePresignedPostRequest;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFilePresignedPostResponse;
import com.example.lionproject2backend.lessonfile.infra.CloudFrontSignedUrlProvider;
import com.example.lionproject2backend.lessonfile.infra.LessonFileStorageClient;
import com.example.lionproject2backend.lessonfile.infra.PresignedPostData;
import com.example.lionproject2backend.lessonfile.infra.S3ObjectMetadata;
import com.example.lionproject2backend.lessonfile.infra.S3ObjectNotFoundException;
import com.example.lionproject2backend.lessonfile.repository.LessonFileRepository;
import com.example.lionproject2backend.lessonfile.service.exception.LessonFileConfirmException;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.repository.UserRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LessonFileService {

    private static final List<LessonFileStatus> COUNTED_UPLOAD_STATUSES = List.of(
            LessonFileStatus.PENDING,
            LessonFileStatus.VALIDATED
    );

    private final LessonFileRepository lessonFileRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LessonFileAccessPolicy accessPolicy;
    private final LessonFileKeyGenerator keyGenerator;
    private final LessonFileStorageClient storageClient;
    private final CloudFrontSignedUrlProvider signedUrlProvider;
    private final LessonFileProperties properties;
    private final Clock clock;

    @Transactional
    public PostLessonFilePresignedPostResponse createPresignedPost(
            Long lessonId,
            Long userId,
            PostLessonFilePresignedPostRequest request
    ) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));
        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        accessPolicy.validateUpload(lesson, userId, request.getType());
        validateUploadRequest(lessonId, request);

        LocalDateTime now = LocalDateTime.now(clock);
        LessonFile file = LessonFile.createPending(
                lesson,
                uploader,
                request.getType(),
                request.getOriginalFileName(),
                request.getContentType(),
                request.getSizeBytes(),
                request.getChecksumSha256(),
                keyGenerator.pendingKey(lessonId),
                now.plusMinutes(properties.getUpload().getPresignedExpireMinutes())
        );

        LessonFile savedFile = lessonFileRepository.save(file);

        PresignedPostData presignedPost = storageClient.createPresignedPost(savedFile, properties);

        return PostLessonFilePresignedPostResponse.of(
                savedFile.getId(),
                presignedPost.uploadUrl(),
                presignedPost.formFields(),
                savedFile.getExpiresAt(),
                properties.getUpload().getMaxSizeBytes(),
                properties.getUpload().getAllowedContentTypes()
        );
    }

    @Transactional(noRollbackFor = LessonFileConfirmException.class)
    public PostLessonFileConfirmResponse confirmUpload(Long fileId, Long userId) {
        LessonFile file = lessonFileRepository.findByIdForUpdate(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_FILE_NOT_FOUND));

        if (file.isValidatedBy(userId)) {
            return PostLessonFileConfirmResponse.from(file);
        }

        if (!file.isUploadedBy(userId)) {
            throw new CustomException(ErrorCode.LESSON_FILE_FORBIDDEN);
        }

        try {
            file.validateConfirmableStatus();
        } catch (IllegalStateException e) {
            throw new CustomException(ErrorCode.LESSON_FILE_INVALID_STATUS);
        }

        accessPolicy.validateAccess(file.getLesson(), userId);
        LocalDateTime now = LocalDateTime.now(clock);

        if (file.getExpiresAt().isBefore(now)) {
            file.expire(now);
            throw new LessonFileConfirmException(ErrorCode.LESSON_FILE_INVALID_STATUS);
        }

        S3ObjectMetadata metadata;
        try {
            metadata = storageClient.headObject(file.getS3Key());
        } catch (S3ObjectNotFoundException e) {
            file.fail(LessonFileFailureCode.S3_OBJECT_NOT_FOUND);
            throw new LessonFileConfirmException(ErrorCode.LESSON_FILE_VALIDATION_FAILED);
        }

        LessonFileFailureCode failureCode = validateHeadObject(file, metadata);

        if (failureCode != null) {
            file.fail(failureCode);
            throw new LessonFileConfirmException(ErrorCode.LESSON_FILE_VALIDATION_FAILED);
        }

        String validatedKey = keyGenerator.validatedKey(file);

        try {
            storageClient.copyObject(file.getS3Key(), validatedKey);
        } catch (RuntimeException e) {
            file.fail(LessonFileFailureCode.S3_COPY_FAILED);
            throw new LessonFileConfirmException(ErrorCode.LESSON_FILE_STORAGE_FAILED);
        }

        file.validate(validatedKey, now);

        return PostLessonFileConfirmResponse.from(file);
    }

    public GetLessonFileListResponse getLessonFiles(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));
        accessPolicy.validateAccess(lesson, userId);

        List<LessonFile> files = lessonFileRepository.findByLessonIdAndStatusOrderByCreatedAtDesc(
                lessonId,
                LessonFileStatus.VALIDATED
        );

        return GetLessonFileListResponse.from(files);
    }

    public GetLessonFileDownloadUrlResponse createDownloadUrl(Long fileId, Long userId) {
        LessonFile file = lessonFileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_FILE_NOT_FOUND));

        accessPolicy.validateAccess(file.getLesson(), userId);

        if (file.getStatus() != LessonFileStatus.VALIDATED || file.getValidatedS3Key() == null) {
            throw new CustomException(ErrorCode.LESSON_FILE_INVALID_STATUS);
        }

        return signedUrlProvider.createSignedUrl(file);
    }

    @Transactional
    public void deleteFile(Long fileId, Long userId) {
        LessonFile file = lessonFileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_FILE_NOT_FOUND));

        if (!file.isUploadedBy(userId)) {
            throw new CustomException(ErrorCode.LESSON_FILE_FORBIDDEN);
        }

        accessPolicy.validateAccess(file.getLesson(), userId);

        try {
            file.delete(LocalDateTime.now(clock));
        } catch (IllegalStateException e) {
            throw new CustomException(ErrorCode.LESSON_FILE_INVALID_STATUS);
        }
    }

    private void validateUploadRequest(Long lessonId, PostLessonFilePresignedPostRequest request) {
        if (request.getSizeBytes() > properties.getUpload().getMaxSizeBytes()) {
            throw new CustomException(ErrorCode.LESSON_FILE_SIZE_EXCEEDED);
        }

        if (!properties.getUpload().getAllowedContentTypes().contains(request.getContentType())) {
            throw new CustomException(ErrorCode.LESSON_FILE_TYPE_NOT_ALLOWED);
        }

        String extension = extractExtension(request.getOriginalFileName());
        if (!properties.getUpload().getAllowedExtensions().contains(extension)) {
            throw new CustomException(ErrorCode.LESSON_FILE_TYPE_NOT_ALLOWED);
        }

        long currentCount = lessonFileRepository.countByLessonIdAndTypeAndStatusIn(
                lessonId,
                request.getType(),
                COUNTED_UPLOAD_STATUSES
        );

        int maxCount = request.getType() == LessonFileType.MATERIAL
                ? properties.getUpload().getMaxMaterialCount()
                : properties.getUpload().getMaxAssignmentCount();

        if (currentCount >= maxCount) {
            throw new CustomException(ErrorCode.LESSON_FILE_COUNT_EXCEEDED);
        }
    }

    private LessonFileFailureCode validateHeadObject(LessonFile file, S3ObjectMetadata metadata) {
        if (metadata.contentLength() != file.getSizeBytes()) {
            return LessonFileFailureCode.SIZE_MISMATCH;
        }

        if (metadata.contentLength() > properties.getUpload().getMaxSizeBytes()) {
            return LessonFileFailureCode.SIZE_LIMIT_EXCEEDED;
        }

        if (!file.getContentType().equals(metadata.contentType())) {
            return LessonFileFailureCode.CONTENT_TYPE_NOT_ALLOWED;
        }

        if (!properties.getUpload().getAllowedContentTypes().contains(metadata.contentType())) {
            return LessonFileFailureCode.CONTENT_TYPE_NOT_ALLOWED;
        }

        Map<String, String> objectMetadata = metadata.metadata();
        if (!String.valueOf(file.getId()).equals(objectMetadata.get("file-id"))) {
            return LessonFileFailureCode.METADATA_MISMATCH;
        }

        if (!String.valueOf(file.getLesson().getId()).equals(objectMetadata.get("lesson-id"))) {
            return LessonFileFailureCode.METADATA_MISMATCH;
        }

        if (!String.valueOf(file.getUploader().getId()).equals(objectMetadata.get("uploader-id"))) {
            return LessonFileFailureCode.METADATA_MISMATCH;
        }

        if (!file.getChecksumSha256().equals(objectMetadata.get("checksum-sha256"))) {
            return LessonFileFailureCode.METADATA_MISMATCH;
        }

        return null;
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}
