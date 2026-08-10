package com.example.lionproject2backend.lessonfile.service;

import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.repository.LessonFileRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LessonFileScheduler {

    private final LessonFileRepository lessonFileRepository;
    private final Clock clock;

    @Scheduled(fixedDelayString = "${lesson-file.upload.pending-expire-scan-delay-ms:86400000}")
    @Transactional
    public void expirePendingFiles() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<LessonFile> files = lessonFileRepository.findExpiredPendingFiles(now);
        files.forEach(file -> file.expire(now));
    }
}
