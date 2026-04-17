package com.example.lionproject2backend.lessonfile.domain;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class LessonFileTest {

    @Test
    void pending_file_can_be_validated() {
        LessonFile file = pendingFile(LessonFileType.MATERIAL);
        LocalDateTime now = LocalDateTime.of(2026, 8, 9, 10, 0);

        file.validate("validated/lessons/1/files/1/upload", now);

        assertThat(file.getStatus()).isEqualTo(LessonFileStatus.VALIDATED);
        assertThat(file.getValidatedS3Key()).isEqualTo("validated/lessons/1/files/1/upload");
        assertThat(file.getUploadedAt()).isEqualTo(now);
        assertThat(file.getValidatedAt()).isEqualTo(now);
    }

    @Test
    void failed_file_cannot_be_validated_again() {
        LessonFile file = pendingFile(LessonFileType.ASSIGNMENT);
        file.fail(LessonFileFailureCode.S3_OBJECT_NOT_FOUND);

        assertThatThrownBy(() -> file.validate("validated/lessons/1/files/1/upload", LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void pending_file_can_be_expired() {
        LessonFile file = pendingFile(LessonFileType.MATERIAL);
        LocalDateTime now = LocalDateTime.of(2026, 8, 9, 10, 5);

        file.expire(now);

        assertThat(file.getStatus()).isEqualTo(LessonFileStatus.EXPIRED);
    }

    @Test
    void validated_file_can_be_deleted() {
        LessonFile file = pendingFile(LessonFileType.MATERIAL);
        LocalDateTime validatedAt = LocalDateTime.of(2026, 8, 9, 10, 0);
        LocalDateTime deletedAt = LocalDateTime.of(2026, 8, 9, 10, 10);
        file.validate("validated/lessons/1/files/1/upload", validatedAt);

        file.delete(deletedAt);

        assertThat(file.getStatus()).isEqualTo(LessonFileStatus.DELETED);
        assertThat(file.getDeletedAt()).isEqualTo(deletedAt);
    }

    private LessonFile pendingFile(LessonFileType type) {
        Lesson lesson = mock(Lesson.class);
        User uploader = User.create("uploader@example.com", "password", "uploader", UserRole.MENTEE);

        return LessonFile.createPending(
                lesson,
                uploader,
                type,
                "spring.pdf",
                "application/pdf",
                1024L,
                "a".repeat(64),
                "pending/lessons/1/files/1/upload",
                LocalDateTime.of(2026, 8, 9, 10, 10)
        );
    }
}
