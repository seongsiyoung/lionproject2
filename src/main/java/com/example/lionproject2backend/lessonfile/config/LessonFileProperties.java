package com.example.lionproject2backend.lessonfile.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "lesson-file")
public class LessonFileProperties {

    private Upload upload = new Upload();
    private S3 s3 = new S3();
    private CloudFront cloudfront = new CloudFront();

    @Getter
    @Setter
    public static class Upload {
        private long maxSizeBytes = 52_428_800L;
        private int presignedExpireMinutes = 10;
        private int downloadExpireMinutes = 10;
        private int maxMaterialCount = 10;
        private int maxAssignmentCount = 10;
        private long pendingExpireScanDelayMs = 86_400_000L;
        private List<String> allowedContentTypes = List.of(
                "application/pdf",
                "image/png",
                "image/jpeg"
        );
        private List<String> allowedExtensions = List.of(
                "pdf",
                "png",
                "jpg",
                "jpeg"
        );
    }

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
        private String region = "ap-northeast-2";
        private String pendingPrefix = "pending";
        private String validatedPrefix = "validated";
        private String failedPrefix = "failed";
    }

    @Getter
    @Setter
    public static class CloudFront {
        private String domain;
        private String keyPairId;
        private String privateKeyPath;
    }
}
