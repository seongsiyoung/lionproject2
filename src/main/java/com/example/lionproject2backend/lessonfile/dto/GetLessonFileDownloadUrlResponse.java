package com.example.lionproject2backend.lessonfile.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GetLessonFileDownloadUrlResponse {

    private Long fileId;
    private String downloadUrl;
    private LocalDateTime expiresAt;

    public static GetLessonFileDownloadUrlResponse of(Long fileId, String downloadUrl, LocalDateTime expiresAt) {
        return GetLessonFileDownloadUrlResponse.builder()
                .fileId(fileId)
                .downloadUrl(downloadUrl)
                .expiresAt(expiresAt)
                .build();
    }
}
