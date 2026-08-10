package com.example.lionproject2backend.lessonfile.infra;

import java.util.Map;

public record PresignedPostData(
        String uploadUrl,
        Map<String, String> formFields
) {
}
