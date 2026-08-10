package com.example.lionproject2backend.lessonfile.config;

import com.example.lionproject2backend.lessonfile.infra.CloudFrontSignedUrlProvider;
import com.example.lionproject2backend.lessonfile.infra.LessonFileStorageClient;
import com.example.lionproject2backend.lessonfile.infra.S3LessonFileStorageClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import static org.assertj.core.api.Assertions.assertThat;

class LessonFileAwsConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(ObjectMapper.class, ObjectMapper::new)
            .withUserConfiguration(
                    LessonFileProperties.class,
                    LessonFileServiceConfig.class,
                    LessonFileAwsConfig.class
            );

    @Test
    void fallback_storage_client_is_used_when_s3_bucket_is_empty() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(LessonFileStorageClient.class);
            assertThat(context.getBean(LessonFileStorageClient.class))
                    .isNotInstanceOf(S3LessonFileStorageClient.class);
            assertThat(context).doesNotHaveBean(AwsCredentialsProvider.class);
        });
    }

    @Test
    void aws_storage_client_replaces_fallback_when_s3_bucket_is_configured() {
        contextRunner
                .withPropertyValues("lesson-file.s3.bucket=lesson-bucket")
                .run(context -> {
                    assertThat(context).hasSingleBean(LessonFileStorageClient.class);
                    assertThat(context.getBean(LessonFileStorageClient.class))
                            .isInstanceOf(S3LessonFileStorageClient.class);
                });
    }

    @Test
    void fallback_cloudfront_provider_is_used_when_cloudfront_config_is_empty() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CloudFrontSignedUrlProvider.class);
            assertThat(context).hasSingleBean(Clock.class);
        });
    }

    @Test
    void aws_cloudfront_provider_replaces_fallback_when_required_values_are_configured() {
        contextRunner
                .withPropertyValues(
                        "lesson-file.cloudfront.domain=d111111abcdef8.cloudfront.net",
                        "lesson-file.cloudfront.key-pair-id=K1234567890",
                        "lesson-file.cloudfront.private-key-path=/tmp/key.pem"
                )
                .run(context -> assertThat(context).hasSingleBean(CloudFrontSignedUrlProvider.class));
    }
}
