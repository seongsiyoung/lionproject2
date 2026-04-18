package com.example.lionproject2backend.lessonfile.infra;

import com.example.lionproject2backend.lessonfile.config.LessonFileProperties;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;

public interface LessonFileStorageClient {

    PresignedPostData createPresignedPost(LessonFile file, LessonFileProperties properties);

    S3ObjectMetadata headObject(String key);

    void copyObject(String sourceKey, String targetKey);
}
