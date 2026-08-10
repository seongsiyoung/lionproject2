package com.example.lionproject2backend.lessonfile.dto;

import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileFailureCode;
import com.example.lionproject2backend.lessonfile.domain.LessonFileStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GetLessonFileListResponse {

    private List<LessonFileItem> files;

    public static GetLessonFileListResponse from(List<LessonFile> files) {
        return GetLessonFileListResponse.builder()
                .files(files.stream()
                        .map(LessonFileItem::from)
                        .toList())
                .build();
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    public static class LessonFileItem {
        private Long fileId;
        private LessonFileType type;
        private LessonFileStatus status;
        private LessonFileFailureCode failureCode;
        private String originalFileName;
        private String contentType;
        private long sizeBytes;
        private Long uploaderId;
        private String uploaderName;
        private LocalDateTime uploadedAt;
        private LocalDateTime validatedAt;
        private LocalDateTime createdAt;

        public static LessonFileItem from(LessonFile file) {
            return LessonFileItem.builder()
                    .fileId(file.getId())
                    .type(file.getType())
                    .status(file.getStatus())
                    .failureCode(file.getFailureCode())
                    .originalFileName(file.getOriginalFileName())
                    .contentType(file.getContentType())
                    .sizeBytes(file.getSizeBytes())
                    .uploaderId(file.getUploader().getId())
                    .uploaderName(file.getUploader().getNickname())
                    .uploadedAt(file.getUploadedAt())
                    .validatedAt(file.getValidatedAt())
                    .createdAt(file.getCreatedAt())
                    .build();
        }
    }
}
