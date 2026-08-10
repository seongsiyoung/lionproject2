package com.example.lionproject2backend.settlement.batch;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.exception.SettlementTargetSkippableException;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import com.example.lionproject2backend.settlement.service.SettlementItemProcessor;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(
        scripts = "classpath:org/springframework/batch/core/schema-h2.sql",
        config = @SqlConfig(errorMode = SqlConfig.ErrorMode.CONTINUE_ON_ERROR)
)
@TestPropertySource(properties = {
        "spring.batch.jdbc.initialize-schema=always",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "jwt.secret=test-secret-test-secret-test-secret-test-secret",
        "jwt.access-exp-ms=3600000",
        "jwt.refresh-exp-ms=86400000",
        "app.cookie.refresh-name=refreshToken",
        "app.cookie.refresh-path=/",
        "app.cookie.refresh-same-site=Lax",
        "app.cookie.refresh-secure=false",
        "portone.store-id=test-store",
        "portone.channel-key=test-channel",
        "portone.api-secret=test-secret",
        "notification.discord.webhook-url=http://localhost/test",
        "notification.slack.webhook-url=http://localhost/test"
})
class SettlementBatchSkipPolicyIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job settlementJob;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SettlementTargetRepository settlementTargetRepository;

    @MockitoBean
    private SettlementItemProcessor settlementItemProcessor;

    @MockitoBean
    private RedissonClient redissonClient;

    @MockitoBean
    private RLock lock;

    @BeforeEach
    void setUpRedissonLock() throws Exception {
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
    }

    @Test
    void target_local_skip_예외는_Job을_계속_진행하고_target을_FAILED로_남긴다() throws Exception {
        persistSettlementDetail("skip-policy-mentor@example.com", "skipPolicyMentee");
        when(settlementItemProcessor.process(any(SettlementTarget.class)))
                .thenAnswer(invocation -> {
                    SettlementTarget target = invocation.getArgument(0);
                    throw new SettlementTargetSkippableException(target, "테스트용 target-local 실패");
                });

        JobExecution jobExecution = launchSettlementJob("2026-07", 101L);

        List<SettlementTarget> targets = settlementTargetRepository.findBySettlementPeriod("2026-07");
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(targets)
                .extracting(SettlementTarget::getStatus)
                .containsOnly(SettlementTarget.SettlementTargetStatus.FAILED);
    }

    @Test
    void DB계열_예외는_skip하지_않고_retry_소진_후_Job을_실패시킨다() throws Exception {
        persistSettlementDetail("db-failure-mentor@example.com", "dbFailureMentee");
        when(settlementItemProcessor.process(any(SettlementTarget.class)))
                .thenThrow(new TransientDataAccessResourceException("temporary db failure"));

        JobExecution jobExecution = launchSettlementJob("2026-07", 102L);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    }

    private JobExecution launchSettlementJob(String settlementPeriod, long runId) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("settlementPeriod", settlementPeriod)
                .addLong("run.id", runId)
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(settlementJob, jobParameters);

        while (jobExecution.isRunning()) {
            Thread.sleep(100);
        }

        return jobExecution;
    }

    private void persistSettlementDetail(String mentorEmail, String menteeNickname) {
        transactionTemplate.executeWithoutResult(status -> {
            User mentorUser = User.create(mentorEmail, "password", mentorEmail, UserRole.MENTOR);
            entityManager.persist(mentorUser);
            Mentor mentor = new Mentor(mentorUser, "career");
            entityManager.persist(mentor);

            Tutorial tutorial = Tutorial.create(mentor, "title", "description", 10000, 60);
            entityManager.persist(tutorial);

            User mentee = User.create(menteeNickname + "@example.com", "password", menteeNickname, UserRole.MENTEE);
            entityManager.persist(mentee);

            Payment payment = Payment.create(tutorial, mentee, 1);
            entityManager.persist(payment);

            SettlementDetail detail = SettlementDetail.createPayment(
                    payment,
                    1000,
                    LocalDateTime.of(2026, 7, 5, 12, 0)
            );
            entityManager.persist(detail);
            entityManager.flush();
        });
    }
}
