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
import com.example.lionproject2backend.user.domain.UserRole;
import com.example.lionproject2backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonFileServiceTest {

    @Mock
    private LessonFileRepository lessonFileRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonFileAccessPolicy accessPolicy;

    @Mock
    private LessonFileKeyGenerator keyGenerator;

    @Mock
    private LessonFileStorageClient storageClient;

    @Mock
    private CloudFrontSignedUrlProvider signedUrlProvider;

    private LessonFileProperties properties;
    private LessonFileService service;

    @BeforeEach
    void setUp() {
        properties = new LessonFileProperties();
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-09T01:00:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        service = new LessonFileService(
                lessonFileRepository,
                lessonRepository,
                userRepository,
                accessPolicy,
                keyGenerator,
                storageClient,
                signedUrlProvider,
                properties,
                clock
        );
    }

    @Test
    void create_presigned_post_saves_pending_file_and_returns_upload_fields() {
        Lesson lesson = mock(Lesson.class);
        User uploader = user(10L);
        PostLessonFilePresignedPostRequest request = presignedRequest("a".repeat(64));

        when(lessonRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(lesson));
        when(userRepository.findById(10L)).thenReturn(Optional.of(uploader));
        when(lessonFileRepository.countByLessonIdAndTypeAndStatusIn(any(), any(), any())).thenReturn(0L);
        when(lessonFileRepository.save(any(LessonFile.class))).thenAnswer(invocation -> {
            LessonFile file = invocation.getArgument(0);
            ReflectionTestUtils.setField(file, "id", 25L);
            return file;
        });
        when(keyGenerator.pendingKey(1L)).thenReturn("pending/lessons/1/files/upload");
        when(storageClient.createPresignedPost(any(LessonFile.class), any(LessonFileProperties.class)))
                .thenReturn(new PresignedPostData(
                        "https://bucket.s3.ap-northeast-2.amazonaws.com",
                        Map.of("key", "pending/lessons/1/files/upload")
                ));

        PostLessonFilePresignedPostResponse response = service.createPresignedPost(1L, 10L, request);

        ArgumentCaptor<LessonFile> fileCaptor = ArgumentCaptor.forClass(LessonFile.class);
        verify(storageClient).createPresignedPost(fileCaptor.capture(), any(LessonFileProperties.class));
        LessonFile savedFile = fileCaptor.getValue();

        assertThat(savedFile.getStatus()).isEqualTo(LessonFileStatus.PENDING);
        assertThat(savedFile.getS3Key()).isEqualTo("pending/lessons/1/files/upload");
        assertThat(response.getFileId()).isEqualTo(25L);
        assertThat(response.getUploadUrl()).isEqualTo("https://bucket.s3.ap-northeast-2.amazonaws.com");
        assertThat(response.getFormFields()).containsEntry("key", "pending/lessons/1/files/upload");
    }

    @Test
    void confirm_upload_validates_head_object_and_marks_validated_after_copy() {
        Lesson lesson = lessonWithId(1L);
        User uploader = user(10L);
        LessonFile file = pendingFile(25L, lesson, uploader, "a".repeat(64));

        when(lessonFileRepository.findByIdForUpdate(25L)).thenReturn(Optional.of(file));
        when(storageClient.headObject("pending/lessons/1/files/25/upload"))
                .thenReturn(new S3ObjectMetadata(
                        1024L,
                        "application/pdf",
                        Map.of(
                                "file-id", "25",
                                "lesson-id", "1",
                                "uploader-id", "10",
                                "checksum-sha256", "a".repeat(64)
                        )
                ));
        when(keyGenerator.validatedKey(file)).thenReturn("validated/lessons/1/files/25/upload");

        PostLessonFileConfirmResponse response = service.confirmUpload(25L, 10L);

        verify(storageClient).copyObject(
                "pending/lessons/1/files/25/upload",
                "validated/lessons/1/files/25/upload"
        );
        assertThat(response.getStatus()).isEqualTo(LessonFileStatus.VALIDATED);
        assertThat(file.getValidatedS3Key()).isEqualTo("validated/lessons/1/files/25/upload");
    }

    @Test
    void confirm_upload_fails_when_metadata_mismatch() {
        Lesson lesson = lessonWithId(1L);
        User uploader = user(10L);
        LessonFile file = pendingFile(25L, lesson, uploader, "a".repeat(64));

        when(lessonFileRepository.findByIdForUpdate(25L)).thenReturn(Optional.of(file));
        when(storageClient.headObject("pending/lessons/1/files/25/upload"))
                .thenReturn(new S3ObjectMetadata(
                        1024L,
                        "application/pdf",
                        Map.of(
                                "file-id", "25",
                                "lesson-id", "1",
                                "uploader-id", "10",
                                "checksum-sha256", "b".repeat(64)
                        )
                ));

        assertThatThrownBy(() -> service.confirmUpload(25L, 10L))
                .isInstanceOf(LessonFileConfirmException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_VALIDATION_FAILED);

        assertThat(file.getStatus()).isEqualTo(LessonFileStatus.FAILED);
        assertThat(file.getFailureCode()).isEqualTo(LessonFileFailureCode.METADATA_MISMATCH);
        verify(storageClient, never()).copyObject(any(), any());
    }

    @Test
    void confirm_upload_marks_failed_when_s3_object_not_found() {
        Lesson lesson = mock(Lesson.class);
        User uploader = user(10L);
        LessonFile file = pendingFile(25L, lesson, uploader, "a".repeat(64));

        when(lessonFileRepository.findByIdForUpdate(25L)).thenReturn(Optional.of(file));
        when(storageClient.headObject("pending/lessons/1/files/25/upload"))
                .thenThrow(new S3ObjectNotFoundException("pending/lessons/1/files/25/upload"));

        assertThatThrownBy(() -> service.confirmUpload(25L, 10L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_VALIDATION_FAILED);

        assertThat(file.getStatus()).isEqualTo(LessonFileStatus.FAILED);
        assertThat(file.getFailureCode()).isEqualTo(LessonFileFailureCode.S3_OBJECT_NOT_FOUND);
        verify(storageClient, never()).copyObject(any(), any());
    }

    @Test
    void expired_pending_file_cannot_be_confirmed() {
        Lesson lesson = mock(Lesson.class);
        User uploader = user(10L);
        LessonFile file = pendingFile(
                25L,
                lesson,
                uploader,
                "a".repeat(64),
                LocalDateTime.of(2026, 8, 9, 9, 59)
        );

        when(lessonFileRepository.findByIdForUpdate(25L)).thenReturn(Optional.of(file));

        assertThatThrownBy(() -> service.confirmUpload(25L, 10L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_INVALID_STATUS);

        assertThat(file.getStatus()).isEqualTo(LessonFileStatus.EXPIRED);
        verify(storageClient, never()).headObject(any());
        verify(storageClient, never()).copyObject(any(), any());
    }

    @Test
    void confirm_upload_is_idempotent_when_already_validated_by_same_uploader() {
        Lesson lesson = mock(Lesson.class);
        User uploader = user(10L);
        LessonFile file = pendingFile(25L, lesson, uploader, "a".repeat(64));
        file.validate("validated/lessons/1/files/25/upload", LocalDateTime.of(2026, 8, 9, 10, 0));

        when(lessonFileRepository.findByIdForUpdate(25L)).thenReturn(Optional.of(file));

        PostLessonFileConfirmResponse response = service.confirmUpload(25L, 10L);

        assertThat(response.getStatus()).isEqualTo(LessonFileStatus.VALIDATED);
        verify(storageClient, never()).headObject(any());
        verify(storageClient, never()).copyObject(any(), any());
    }

    @Test
    void non_uploader_cannot_confirm_upload() {
        Lesson lesson = mock(Lesson.class);
        User uploader = user(10L);
        LessonFile file = pendingFile(25L, lesson, uploader, "a".repeat(64));

        when(lessonFileRepository.findByIdForUpdate(25L)).thenReturn(Optional.of(file));

        assertThatThrownBy(() -> service.confirmUpload(25L, 20L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_FORBIDDEN);

        verify(storageClient, never()).headObject(any());
        verify(storageClient, never()).copyObject(any(), any());
    }

    @Test
    void download_url_requires_participant_and_validated_status() {
        Lesson lesson = mock(Lesson.class);
        User uploader = user(10L);
        LessonFile file = pendingFile(25L, lesson, uploader, "a".repeat(64));
        file.validate("validated/lessons/1/files/25/upload", LocalDateTime.of(2026, 8, 9, 10, 0));

        when(lessonFileRepository.findById(25L)).thenReturn(Optional.of(file));
        when(signedUrlProvider.createSignedUrl(file))
                .thenReturn(GetLessonFileDownloadUrlResponse.of(
                        25L,
                        "https://cdn.example.com/validated/lessons/1/files/25/upload",
                        LocalDateTime.of(2026, 8, 9, 10, 10)
                ));

        GetLessonFileDownloadUrlResponse response = service.createDownloadUrl(25L, 10L);

        verify(accessPolicy).validateAccess(lesson, 10L);
        assertThat(response.getDownloadUrl()).isEqualTo("https://cdn.example.com/validated/lessons/1/files/25/upload");
    }

    @Test
    void get_lesson_files_returns_validated_files_only() {
        Lesson lesson = mock(Lesson.class);
        LessonFile validatedFile = pendingFile(25L, lesson, user(10L), "a".repeat(64));
        validatedFile.validate("validated/lessons/1/files/25/upload", LocalDateTime.of(2026, 8, 9, 10, 0));

        when(lessonRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(lesson));
        when(lessonFileRepository.findByLessonIdAndStatusOrderByCreatedAtDesc(1L, LessonFileStatus.VALIDATED))
                .thenReturn(List.of(validatedFile));

        var response = service.getLessonFiles(1L, 20L);

        verify(accessPolicy).validateAccess(lesson, 20L);
        assertThat(response.getFiles())
                .extracting("fileId")
                .containsExactly(25L);
    }

    @Test
    void copy_failure_does_not_leave_file_validated() {
        Lesson lesson = lessonWithId(1L);
        User uploader = user(10L);
        LessonFile file = pendingFile(25L, lesson, uploader, "a".repeat(64));

        when(lessonFileRepository.findByIdForUpdate(25L)).thenReturn(Optional.of(file));
        when(storageClient.headObject("pending/lessons/1/files/25/upload"))
                .thenReturn(new S3ObjectMetadata(
                        1024L,
                        "application/pdf",
                        Map.of(
                                "file-id", "25",
                                "lesson-id", "1",
                                "uploader-id", "10",
                                "checksum-sha256", "a".repeat(64)
                        )
                ));
        when(keyGenerator.validatedKey(file)).thenReturn("validated/lessons/1/files/25/upload");
        doThrow(new IllegalStateException("copy failed"))
                .when(storageClient)
                .copyObject("pending/lessons/1/files/25/upload", "validated/lessons/1/files/25/upload");

        assertThatThrownBy(() -> service.confirmUpload(25L, 10L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_STORAGE_FAILED);

        assertThat(file.getStatus()).isEqualTo(LessonFileStatus.FAILED);
        assertThat(file.getFailureCode()).isEqualTo(LessonFileFailureCode.S3_COPY_FAILED);
    }

    private PostLessonFilePresignedPostRequest presignedRequest(String checksum) {
        PostLessonFilePresignedPostRequest request = new PostLessonFilePresignedPostRequest();
        ReflectionTestUtils.setField(request, "type", LessonFileType.MATERIAL);
        ReflectionTestUtils.setField(request, "originalFileName", "spring.pdf");
        ReflectionTestUtils.setField(request, "contentType", "application/pdf");
        ReflectionTestUtils.setField(request, "sizeBytes", 1024L);
        ReflectionTestUtils.setField(request, "checksumSha256", checksum);
        return request;
    }

    private LessonFile pendingFile(Long fileId, Lesson lesson, User uploader, String checksum) {
        return pendingFile(fileId, lesson, uploader, checksum, LocalDateTime.of(2026, 8, 9, 10, 10));
    }

    private LessonFile pendingFile(
            Long fileId,
            Lesson lesson,
            User uploader,
            String checksum,
            LocalDateTime expiresAt
    ) {
        LessonFile file = LessonFile.createPending(
                lesson,
                uploader,
                LessonFileType.MATERIAL,
                "spring.pdf",
                "application/pdf",
                1024L,
                checksum,
                "pending/lessons/1/files/25/upload",
                expiresAt
        );
        ReflectionTestUtils.setField(file, "id", fileId);
        return file;
    }

    private Lesson lessonWithId(Long lessonId) {
        Lesson lesson = mock(Lesson.class);
        when(lesson.getId()).thenReturn(lessonId);
        return lesson;
    }

    private User user(Long userId) {
        User user = User.create("uploader@example.com", "password", "uploader", UserRole.MENTEE);
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }
}
