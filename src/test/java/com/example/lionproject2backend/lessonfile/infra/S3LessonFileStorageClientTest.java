package com.example.lionproject2backend.lessonfile.infra;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lessonfile.config.LessonFileProperties;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import com.example.lionproject2backend.user.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3LessonFileStorageClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-09T01:02:03Z"),
            ZoneId.of("Asia/Seoul")
    );

    @Test
    void create_presigned_post_builds_form_fields_and_policy_conditions() throws Exception {
        S3LessonFileStorageClient client = new S3LessonFileStorageClient(
                null,
                StaticCredentialsProvider.create(AwsBasicCredentials.create("AKIATEST", "secret")),
                properties(),
                objectMapper,
                clock
        );
        LessonFileProperties properties = properties();
        LessonFile file = pendingFile();

        PresignedPostData data = client.createPresignedPost(file, properties);

        assertThat(data.uploadUrl()).isEqualTo("https://lesson-bucket.s3.ap-northeast-2.amazonaws.com");
        assertThat(data.formFields())
                .containsEntry("key", "pending/lessons/1/files/upload")
                .containsEntry("Content-Type", "application/pdf")
                .containsEntry("x-amz-algorithm", "AWS4-HMAC-SHA256")
                .containsEntry("x-amz-credential", "AKIATEST/20260809/ap-northeast-2/s3/aws4_request")
                .containsEntry("x-amz-date", "20260809T010203Z")
                .containsEntry("x-amz-meta-file-id", "25")
                .containsEntry("x-amz-meta-lesson-id", "1")
                .containsEntry("x-amz-meta-uploader-id", "10")
                .containsEntry("x-amz-meta-checksum-sha256", "a".repeat(64));
        assertThat(data.formFields().get("policy")).isNotBlank();
        assertThat(data.formFields().get("x-amz-signature")).hasSize(64);

        JsonNode policy = decodePolicy(data.formFields().get("policy"));
        assertThat(policy.get("expiration").asText()).isEqualTo("2026-08-09T01:12:03Z");
        assertThat(policy.get("conditions").toString())
                .contains("\"bucket\":\"lesson-bucket\"")
                .contains("\"key\":\"pending/lessons/1/files/upload\"")
                .contains("[\"content-length-range\",1,52428800]")
                .contains("\"x-amz-meta-file-id\":\"25\"");
    }

    @Test
    void create_presigned_post_includes_security_token_for_temporary_credentials() {
        S3LessonFileStorageClient client = new S3LessonFileStorageClient(
                null,
                StaticCredentialsProvider.create(AwsSessionCredentials.create("AKIATEST", "secret", "token")),
                properties(),
                objectMapper,
                clock
        );

        PresignedPostData data = client.createPresignedPost(pendingFile(), properties());

        assertThat(data.formFields()).containsEntry("x-amz-security-token", "token");
    }

    @Test
    void head_object_uses_configured_bucket_and_maps_s3_metadata() {
        S3Client s3Client = mock(S3Client.class);
        S3LessonFileStorageClient client = new S3LessonFileStorageClient(
                s3Client,
                StaticCredentialsProvider.create(AwsBasicCredentials.create("AKIATEST", "secret")),
                properties(),
                objectMapper,
                clock
        );
        when(s3Client.headObject(HeadObjectRequest.builder()
                .bucket("lesson-bucket")
                .key("pending/lessons/1/files/upload")
                .build()))
                .thenReturn(HeadObjectResponse.builder()
                        .contentLength(1024L)
                        .contentType("application/pdf")
                        .metadata(Map.of("file-id", "25"))
                        .build());

        S3ObjectMetadata metadata = client.headObject("pending/lessons/1/files/upload");

        assertThat(metadata.contentLength()).isEqualTo(1024L);
        assertThat(metadata.contentType()).isEqualTo("application/pdf");
        assertThat(metadata.metadata()).containsEntry("file-id", "25");
    }

    @Test
    void copy_object_uses_configured_bucket_for_source_and_destination() {
        S3Client s3Client = mock(S3Client.class);
        S3LessonFileStorageClient client = new S3LessonFileStorageClient(
                s3Client,
                StaticCredentialsProvider.create(AwsBasicCredentials.create("AKIATEST", "secret")),
                properties(),
                objectMapper,
                clock
        );

        client.copyObject("pending/lessons/1/files/upload", "validated/lessons/1/files/upload");

        verify(s3Client).copyObject(CopyObjectRequest.builder()
                .sourceBucket("lesson-bucket")
                .sourceKey("pending/lessons/1/files/upload")
                .destinationBucket("lesson-bucket")
                .destinationKey("validated/lessons/1/files/upload")
                .build());
    }

    private JsonNode decodePolicy(String policy) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(policy);
        return objectMapper.readTree(new String(decoded, StandardCharsets.UTF_8));
    }

    private LessonFileProperties properties() {
        LessonFileProperties properties = new LessonFileProperties();
        properties.getS3().setBucket("lesson-bucket");
        properties.getS3().setRegion("ap-northeast-2");
        return properties;
    }

    private LessonFile pendingFile() {
        Lesson lesson = mock(Lesson.class);
        User uploader = mock(User.class);
        org.mockito.Mockito.when(lesson.getId()).thenReturn(1L);
        org.mockito.Mockito.when(uploader.getId()).thenReturn(10L);
        LessonFile file = LessonFile.createPending(
                lesson,
                uploader,
                LessonFileType.MATERIAL,
                "spring.pdf",
                "application/pdf",
                1024L,
                "a".repeat(64),
                "pending/lessons/1/files/upload",
                LocalDateTime.of(2026, 8, 9, 10, 30)
        );
        ReflectionTestUtils.setField(file, "id", 25L);
        return file;
    }
}
