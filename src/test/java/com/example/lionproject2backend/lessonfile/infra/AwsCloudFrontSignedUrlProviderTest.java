package com.example.lionproject2backend.lessonfile.infra;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lessonfile.config.LessonFileProperties;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileDownloadUrlResponse;
import com.example.lionproject2backend.user.domain.User;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AwsCloudFrontSignedUrlProviderTest {

    @Test
    void create_signed_url_returns_cloudfront_url_with_canned_policy() throws Exception {
        Path privateKey = writePrivateKey();
        LessonFileProperties properties = new LessonFileProperties();
        properties.getCloudfront().setDomain("d111111abcdef8.cloudfront.net");
        properties.getCloudfront().setKeyPairId("K1234567890");
        properties.getCloudfront().setPrivateKeyPath(privateKey.toString());
        Clock clock = Clock.fixed(Instant.parse("2026-08-09T01:00:00Z"), ZoneId.of("Asia/Seoul"));
        AwsCloudFrontSignedUrlProvider provider = new AwsCloudFrontSignedUrlProvider(properties, clock);

        GetLessonFileDownloadUrlResponse response = provider.createSignedUrl(validatedFile());

        assertThat(response.getDownloadUrl())
                .startsWith("https://d111111abcdef8.cloudfront.net/validated/lessons/1/files/upload?")
                .contains("Expires=")
                .contains("Signature=")
                .contains("Key-Pair-Id=K1234567890");
        assertThat(response.getExpiresAt()).isEqualTo(LocalDateTime.of(2026, 8, 9, 10, 10));
    }

    private Path writePrivateKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes())
                .encodeToString(keyPair.getPrivate().getEncoded());
        String pem = "-----BEGIN PRIVATE KEY-----\n" + base64 + "\n-----END PRIVATE KEY-----\n";
        Path path = Files.createTempFile("cloudfront-private-key", ".pem");
        Files.writeString(path, pem);
        return path;
    }

    private LessonFile validatedFile() {
        LessonFile file = LessonFile.createPending(
                mock(Lesson.class),
                mock(User.class),
                LessonFileType.MATERIAL,
                "spring.pdf",
                "application/pdf",
                1024L,
                "a".repeat(64),
                "pending/lessons/1/files/upload",
                LocalDateTime.of(2026, 8, 9, 10, 30)
        );
        file.validate("validated/lessons/1/files/upload", LocalDateTime.of(2026, 8, 9, 10, 0));
        return file;
    }
}
