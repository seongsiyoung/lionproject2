package com.example.lionproject2backend.lessonfile.config;

import com.example.lionproject2backend.lessonfile.infra.AwsCloudFrontSignedUrlProvider;
import com.example.lionproject2backend.lessonfile.infra.CloudFrontSignedUrlProvider;
import com.example.lionproject2backend.lessonfile.infra.LessonFileStorageClient;
import com.example.lionproject2backend.lessonfile.infra.S3LessonFileStorageClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class LessonFileAwsConfig {

    private final LessonFileProperties properties;

    @Bean
    @ConditionalOnExpression("'${lesson-file.s3.bucket:}' != ''")
    public AwsCredentialsProvider awsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    @ConditionalOnExpression("'${lesson-file.s3.bucket:}' != ''")
    public S3Client lessonFileS3Client(AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .region(Region.of(properties.getS3().getRegion()))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    @ConditionalOnExpression("'${lesson-file.s3.bucket:}' != ''")
    public LessonFileStorageClient lessonFileStorageClient(
            S3Client lessonFileS3Client,
            AwsCredentialsProvider awsCredentialsProvider,
            ObjectMapper objectMapper,
            Clock clock
    ) {
        return new S3LessonFileStorageClient(
                lessonFileS3Client,
                awsCredentialsProvider,
                properties,
                objectMapper,
                clock
        );
    }

    @Bean
    @ConditionalOnExpression("'${lesson-file.cloudfront.domain:}' != '' "
            + "&& '${lesson-file.cloudfront.key-pair-id:}' != '' "
            + "&& '${lesson-file.cloudfront.private-key-path:}' != ''")
    public CloudFrontSignedUrlProvider cloudFrontSignedUrlProvider(Clock clock) {
        return new AwsCloudFrontSignedUrlProvider(properties, clock);
    }
}
