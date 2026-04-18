package com.example.lionproject2backend.lessonfile.service;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import com.example.lionproject2backend.lessonfile.repository.LessonFileRepository;
import com.example.lionproject2backend.user.domain.User;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonFileSchedulerTest {

    @Mock
    private LessonFileRepository lessonFileRepository;

    @Test
    void expire_pending_files_marks_expired_pending_files() {
        Clock clock = Clock.fixed(Instant.parse("2026-08-09T01:00:00Z"), ZoneId.of("Asia/Seoul"));
        LessonFileScheduler scheduler = new LessonFileScheduler(lessonFileRepository, clock);
        LessonFile expiredFile = pendingFile(LocalDateTime.of(2026, 8, 9, 9, 59));

        when(lessonFileRepository.findExpiredPendingFiles(LocalDateTime.of(2026, 8, 9, 10, 0)))
                .thenReturn(List.of(expiredFile));

        scheduler.expirePendingFiles();

        verify(lessonFileRepository).findExpiredPendingFiles(LocalDateTime.of(2026, 8, 9, 10, 0));
        assertThat(expiredFile.getStatus()).isEqualTo(LessonFileStatus.EXPIRED);
    }

    private LessonFile pendingFile(LocalDateTime expiresAt) {
        return LessonFile.createPending(
                mock(Lesson.class),
                mock(User.class),
                LessonFileType.MATERIAL,
                "spring.pdf",
                "application/pdf",
                1024L,
                "a".repeat(64),
                "pending/lessons/1/files/upload",
                expiresAt
        );
    }
}
