package com.example.lionproject2backend.lessonfile.infra;

import com.example.lionproject2backend.lessonfile.config.LessonFileProperties;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RequiredArgsConstructor
public class S3LessonFileStorageClient implements LessonFileStorageClient {

    private static final DateTimeFormatter AMZ_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter SHORT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final S3Client s3Client;
    private final AwsCredentialsProvider credentialsProvider;
    private final LessonFileProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Override
    public PresignedPostData createPresignedPost(LessonFile file, LessonFileProperties requestProperties) {
        AwsCredentials credentials = credentialsProvider.resolveCredentials();
        Instant now = Instant.now(clock);
        String shortDate = SHORT_DATE_FORMATTER.format(now);
        String amzDate = AMZ_DATE_FORMATTER.format(now);
        String credentialScope = shortDate + "/" + requestProperties.getS3().getRegion() + "/s3/aws4_request";
        String credential = credentials.accessKeyId() + "/" + credentialScope;

        Map<String, String> fields = new LinkedHashMap<>();
        fields.put( "key", file.getS3Key());
        fields.put("Content-Type", file.getContentType());
        fields.put("x-amz-algorithm", "AWS4-HMAC-SHA256");
        fields.put("x-amz-credential", credential);
        fields.put("x-amz-date", amzDate);
        fields.put("x-amz-meta-file-id", String.valueOf(file.getId()));
        fields.put("x-amz-meta-lesson-id", String.valueOf(file.getLesson().getId()));
        fields.put("x-amz-meta-uploader-id", String.valueOf(file.getUploader().getId()));
        fields.put("x-amz-meta-checksum-sha256", file.getChecksumSha256());
        if (credentials instanceof AwsSessionCredentials sessionCredentials) {
            fields.put("x-amz-security-token", sessionCredentials.sessionToken());
        }

        String policy = createPolicy(requestProperties, fields, now);
        fields.put("policy", policy);
        fields.put("x-amz-signature", signPolicy(policy, credentials.secretAccessKey(), shortDate, requestProperties.getS3().getRegion()));

        return new PresignedPostData(uploadUrl(requestProperties), fields);
    }

    @Override
    public S3ObjectMetadata headObject(String key) {
        try {
            HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(properties.getS3().getBucket())
                    .key(key)
                    .build());
            return new S3ObjectMetadata(
                    response.contentLength(),
                    response.contentType(),
                    response.metadata()
            );
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new S3ObjectNotFoundException(key);
            }
            throw e;
        }
    }

    @Override
    public void copyObject(String sourceKey, String targetKey) {
        s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(properties.getS3().getBucket())
                .sourceKey(sourceKey)
                .destinationBucket(properties.getS3().getBucket())
                .destinationKey(targetKey)
                .build());
    }

    private String createPolicy(
            LessonFileProperties properties,
            Map<String, String> fields,
            Instant now
    ) {
        ObjectNode policy = objectMapper.createObjectNode();
        policy.put("expiration", now.plus(Duration.ofMinutes(properties.getUpload().getPresignedExpireMinutes())).toString());

        ArrayNode conditions = policy.putArray("conditions");
        addCondition(conditions, "bucket", properties.getS3().getBucket());
        fields.forEach((key, value) -> addCondition(conditions, key, value));

        ArrayNode contentLengthRange = conditions.addArray();
        contentLengthRange.add("content-length-range");
        contentLengthRange.add(1);
        contentLengthRange.add(properties.getUpload().getMaxSizeBytes());

        try {
            String policyJson = objectMapper.writeValueAsString(policy);
            return Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw SdkClientException.create("Failed to create S3 POST policy.", e);
        }
    }

    private void addCondition(ArrayNode conditions, String key, String value) {
        ObjectNode condition = objectMapper.createObjectNode();
        condition.put(key, value);
        conditions.add(condition);
    }

    private String uploadUrl(LessonFileProperties properties) {
        return "https://" + properties.getS3().getBucket()
                + ".s3." + properties.getS3().getRegion()
                + ".amazonaws.com";
    }

    private String signPolicy(String policy, String secretAccessKey, String shortDate, String region) {
        byte[] dateKey = hmacSha256(("AWS4" + secretAccessKey).getBytes(StandardCharsets.UTF_8), shortDate);
        byte[] dateRegionKey = hmacSha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacSha256(dateRegionKey, "s3");
        byte[] signingKey = hmacSha256(dateRegionServiceKey, "aws4_request");
        return hex(hmacSha256(signingKey, policy));
    }

    private byte[] hmacSha256(byte[] key, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw SdkClientException.create("Failed to sign S3 POST policy.", e);
        }
    }

    private String hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
