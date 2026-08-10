package com.example.lionproject2backend.lessonfile.infra;

import java.util.Map;

public record S3ObjectMetadata(
        long contentLength,
        String contentType,
        Map<String, String> metadata
) {
}
