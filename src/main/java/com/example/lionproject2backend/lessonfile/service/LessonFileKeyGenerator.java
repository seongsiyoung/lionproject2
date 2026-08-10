package com.example.lionproject2backend.lessonfile.service;

import com.example.lionproject2backend.lessonfile.config.LessonFileProperties;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonFileKeyGenerator {

    private final LessonFileProperties properties;

    public String pendingKey(Long lessonId) {
        return "%s/lessons/%d/files/%s".formatted(
                properties.getS3().getPendingPrefix(),
                lessonId,
                UUID.randomUUID()
        );
    }

    public String validatedKey(LessonFile file) {
        String pendingPrefix = properties.getS3().getPendingPrefix() + "/";
        String validatedPrefix = properties.getS3().getValidatedPrefix() + "/";

        if (file.getS3Key().startsWith(pendingPrefix)) {
            return validatedPrefix + file.getS3Key().substring(pendingPrefix.length());
        }

        return "%s/%s".formatted(properties.getS3().getValidatedPrefix(), file.getS3Key());
    }
}
