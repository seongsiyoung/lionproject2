package com.example.lionproject2backend.lessonfile.dto;

import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileFailureCode;
import com.example.lionproject2backend.lessonfile.domain.LessonFileStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PostLessonFileConfirmResponse {

    private Long fileId;
    private LessonFileStatus status;
    private LessonFileFailureCode failureCode;
    private String originalFileName;
    private String contentType;
    private long sizeBytes;

    public static PostLessonFileConfirmResponse from(LessonFile file) {
        return PostLessonFileConfirmResponse.builder()
                .fileId(file.getId())
                .status(file.getStatus())
                .failureCode(file.getFailureCode())
                .originalFileName(file.getOriginalFileName())
                .contentType(file.getContentType())
                .sizeBytes(file.getSizeBytes())
                .build();
    }
}
