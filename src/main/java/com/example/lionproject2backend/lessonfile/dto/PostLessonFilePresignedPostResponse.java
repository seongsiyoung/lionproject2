package com.example.lionproject2backend.lessonfile.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PostLessonFilePresignedPostResponse {

    private Long fileId;
    private String uploadUrl;
    private Map<String, String> formFields;
    private LocalDateTime expiresAt;
    private long maxSizeBytes;
    private List<String> allowedContentTypes;

    public static PostLessonFilePresignedPostResponse of(
            Long fileId,
            String uploadUrl,
            Map<String, String> formFields,
            LocalDateTime expiresAt,
            long maxSizeBytes,
            List<String> allowedContentTypes
    ) {
        return PostLessonFilePresignedPostResponse.builder()
                .fileId(fileId)
                .uploadUrl(uploadUrl)
                .formFields(formFields)
                .expiresAt(expiresAt)
                .maxSizeBytes(maxSizeBytes)
                .allowedContentTypes(allowedContentTypes)
                .build();
    }
}
