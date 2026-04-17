package com.example.lionproject2backend.lessonfile.domain;

public enum LessonFileFailureCode {
    S3_OBJECT_NOT_FOUND,
    S3_KEY_MISMATCH,
    SIZE_MISMATCH,
    SIZE_LIMIT_EXCEEDED,
    CONTENT_TYPE_NOT_ALLOWED,
    EXTENSION_NOT_ALLOWED,
    METADATA_MISMATCH,
    S3_COPY_FAILED
}
