package com.example.lionproject2backend.lessonfile.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lesson_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LessonFileType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LessonFileStatus status;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "checksum_sha256", nullable = false, length = 64)
    private String checksumSha256;

    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    @Column(name = "validated_s3_key", length = 500)
    private String validatedS3Key;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_code", length = 50)
    private LessonFileFailureCode failureCode;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public static LessonFile createPending(
            Lesson lesson,
            User uploader,
            LessonFileType type,
            String originalFileName,
            String contentType,
            long sizeBytes,
            String checksumSha256,
            String s3Key,
            LocalDateTime expiresAt
    ) {
        LessonFile file = new LessonFile();
        file.lesson = lesson;
        file.uploader = uploader;
        file.type = type;
        file.status = LessonFileStatus.PENDING;
        file.originalFileName = originalFileName;
        file.contentType = contentType;
        file.sizeBytes = sizeBytes;
        file.checksumSha256 = checksumSha256;
        file.s3Key = s3Key;
        file.expiresAt = expiresAt;
        return file;
    }

    public void validate(String validatedS3Key, LocalDateTime now) {
        validatePendingStatus();
        this.status = LessonFileStatus.VALIDATED;
        this.validatedS3Key = validatedS3Key;
        this.uploadedAt = now;
        this.validatedAt = now;
        this.failureCode = null;
    }

    public void fail(LessonFileFailureCode failureCode) {
        validatePendingStatus();
        this.status = LessonFileStatus.FAILED;
        this.failureCode = failureCode;
    }

    public void expire(LocalDateTime now) {
        validatePendingStatus();
        this.status = LessonFileStatus.EXPIRED;
    }

    public void delete(LocalDateTime now) {
        if (this.status != LessonFileStatus.PENDING && this.status != LessonFileStatus.VALIDATED) {
            throw new IllegalStateException("삭제할 수 없는 파일 상태입니다.");
        }
        this.status = LessonFileStatus.DELETED;
        this.deletedAt = now;
    }

    private void validatePendingStatus() {
        if (this.status != LessonFileStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 파일만 처리할 수 있습니다.");
        }
    }
}
