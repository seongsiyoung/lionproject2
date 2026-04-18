package com.example.lionproject2backend.lessonfile.config;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileDownloadUrlResponse;
import com.example.lionproject2backend.lessonfile.infra.CloudFrontSignedUrlProvider;
import com.example.lionproject2backend.lessonfile.infra.LessonFileStorageClient;
import com.example.lionproject2backend.lessonfile.infra.PresignedPostData;
import com.example.lionproject2backend.lessonfile.infra.S3ObjectMetadata;
import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LessonFileServiceConfig {

    @Bean
    @ConditionalOnMissingBean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("'${lesson-file.s3.bucket:}' == ''")
    public LessonFileStorageClient unavailableLessonFileStorageClient() {
        return new LessonFileStorageClient() {
            @Override
            public PresignedPostData createPresignedPost(LessonFile file, LessonFileProperties properties) {
                throw new CustomException(ErrorCode.LESSON_FILE_STORAGE_FAILED);
            }

            @Override
            public S3ObjectMetadata headObject(String key) {
                throw new CustomException(ErrorCode.LESSON_FILE_STORAGE_FAILED);
            }

            @Override
            public void copyObject(String sourceKey, String targetKey) {
                throw new CustomException(ErrorCode.LESSON_FILE_STORAGE_FAILED);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("'${lesson-file.cloudfront.domain:}' == '' "
            + "|| '${lesson-file.cloudfront.key-pair-id:}' == '' "
            + "|| '${lesson-file.cloudfront.private-key-path:}' == ''")
    public CloudFrontSignedUrlProvider unavailableCloudFrontSignedUrlProvider() {
        return new CloudFrontSignedUrlProvider() {
            @Override
            public GetLessonFileDownloadUrlResponse createSignedUrl(LessonFile file) {
                throw new CustomException(ErrorCode.LESSON_FILE_STORAGE_FAILED);
            }
        };
    }
}
