package com.example.lionproject2backend.lessonfile.infra;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.lessonfile.config.LessonFileProperties;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileDownloadUrlResponse;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

@RequiredArgsConstructor
public class AwsCloudFrontSignedUrlProvider implements CloudFrontSignedUrlProvider {

    private final LessonFileProperties properties;
    private final Clock clock;
    private final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

    @Override
    public GetLessonFileDownloadUrlResponse createSignedUrl(LessonFile file) {
        Instant expiresAt = Instant.now(clock)
                .plusSeconds(properties.getUpload().getDownloadExpireMinutes() * 60L);
        String resourceUrl = "https://" + properties.getCloudfront().getDomain()
                + "/" + file.getValidatedS3Key();

        SignedUrl signedUrl = createSignedUrl(resourceUrl, expiresAt);

        return GetLessonFileDownloadUrlResponse.of(
                file.getId(),
                signedUrl.url(),
                LocalDateTime.ofInstant(expiresAt, clock.getZone())
        );
    }

    private SignedUrl createSignedUrl(String resourceUrl, Instant expiresAt) {
        try {
            CannedSignerRequest request = CannedSignerRequest.builder()
                    .resourceUrl(resourceUrl)
                    .privateKey(Path.of(properties.getCloudfront().getPrivateKeyPath()))
                    .keyPairId(properties.getCloudfront().getKeyPairId())
                    .expirationDate(expiresAt)
                    .build();
            return cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.LESSON_FILE_STORAGE_FAILED);
        }
    }

}
