package com.example.lionproject2backend.lessonfile.service;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileFailureCode;
import com.example.lionproject2backend.lessonfile.domain.LessonFileStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import com.example.lionproject2backend.lessonfile.infra.LessonFileStorageClient;
import com.example.lionproject2backend.lessonfile.infra.S3ObjectMetadata;
import com.example.lionproject2backend.lessonfile.repository.LessonFileRepository;
import com.example.lionproject2backend.lessonfile.service.exception.LessonFileConfirmException;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.ticket.domain.Ticket;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = {
        "notification.discord.webhook-url=test",
        "notification.slack.webhook-url=test"
})
class LessonFileServicePersistenceTest {

    @Autowired
    private LessonFileService lessonFileService;

    @Autowired
    private LessonFileRepository lessonFileRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private LessonFileStorageClient storageClient;

    @MockitoBean
    private RedissonClient redissonClient;

    @Test
    void metadata_mismatch_failure_is_persisted_after_confirm_exception() {
        PersistedFile fixture = createPendingFile("pending/lessons/1/files/mismatch");
        when(storageClient.headObject(fixture.s3Key()))
                .thenReturn(new S3ObjectMetadata(
                        1024L,
                        "application/pdf",
                        Map.of(
                                "file-id", String.valueOf(fixture.fileId()),
                                "lesson-id", String.valueOf(fixture.lessonId()),
                                "uploader-id", String.valueOf(fixture.uploaderId()),
                                "checksum-sha256", "b".repeat(64)
                        )
                ));

        assertThatThrownBy(() -> lessonFileService.confirmUpload(fixture.fileId(), fixture.uploaderId()))
                .isInstanceOf(LessonFileConfirmException.class);

        LessonFile reloaded = lessonFileRepository.findById(fixture.fileId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(LessonFileStatus.FAILED);
        assertThat(reloaded.getFailureCode()).isEqualTo(LessonFileFailureCode.METADATA_MISMATCH);
    }

    @Test
    void copy_failure_is_persisted_after_confirm_exception() {
        PersistedFile fixture = createPendingFile("pending/lessons/1/files/copy-failure");
        when(storageClient.headObject(fixture.s3Key()))
                .thenReturn(new S3ObjectMetadata(
                        1024L,
                        "application/pdf",
                        Map.of(
                                "file-id", String.valueOf(fixture.fileId()),
                                "lesson-id", String.valueOf(fixture.lessonId()),
                                "uploader-id", String.valueOf(fixture.uploaderId()),
                                "checksum-sha256", "a".repeat(64)
                        )
                ));
        doThrow(new IllegalStateException("copy failed"))
                .when(storageClient)
                .copyObject(fixture.s3Key(), "validated/lessons/1/files/copy-failure");

        assertThatThrownBy(() -> lessonFileService.confirmUpload(fixture.fileId(), fixture.uploaderId()))
                .isInstanceOf(LessonFileConfirmException.class);

        LessonFile reloaded = lessonFileRepository.findById(fixture.fileId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(LessonFileStatus.FAILED);
        assertThat(reloaded.getFailureCode()).isEqualTo(LessonFileFailureCode.S3_COPY_FAILED);
    }

    private PersistedFile createPendingFile(String s3Key) {
        return transactionTemplate.execute(status -> {
            User mentorUser = User.create("mentor-" + s3Key.hashCode() + "@example.com", "password", "mentor" + Math.abs(s3Key.hashCode()), UserRole.MENTOR);
            User mentee = User.create("mentee-" + s3Key.hashCode() + "@example.com", "password", "mentee" + Math.abs(s3Key.hashCode()), UserRole.MENTEE);
            entityManager.persist(mentorUser);
            entityManager.persist(mentee);

            Mentor mentor = new Mentor(mentorUser, "career");
            mentor.approve();
            entityManager.persist(mentor);

            Tutorial tutorial = Tutorial.create(mentor, "Spring", "Spring lesson", 10_000, 60);
            entityManager.persist(tutorial);

            Payment payment = Payment.create(tutorial, mentee, 1);
            entityManager.persist(payment);

            Ticket ticket = Ticket.create(payment, tutorial, mentee, 1);
            entityManager.persist(ticket);

            Lesson lesson = Lesson.register(ticket, "request", LocalDateTime.now().plusDays(1));
            lesson.confirm(mentorUser.getId());
            entityManager.persist(lesson);

            LessonFile file = LessonFile.createPending(
                    lesson,
                    mentorUser,
                    LessonFileType.MATERIAL,
                    "spring.pdf",
                    "application/pdf",
                    1024L,
                    "a".repeat(64),
                    s3Key,
                    LocalDateTime.now().plusMinutes(10)
            );
            entityManager.persist(file);
            entityManager.flush();

            return new PersistedFile(file.getId(), lesson.getId(), mentorUser.getId(), s3Key);
        });
    }

    private record PersistedFile(Long fileId, Long lessonId, Long uploaderId, String s3Key) {
    }
}
