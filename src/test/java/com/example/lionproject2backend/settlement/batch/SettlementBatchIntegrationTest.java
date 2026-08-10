package com.example.lionproject2backend.settlement.batch;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
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
class SettlementBatchIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job settlementJob;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private SettlementDetailRepository settlementDetailRepository;

    @Autowired
    private SettlementTargetRepository settlementTargetRepository;

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
    void 같은_정산월을_다시_실행하면_PENDING_정산을_재계산하고_미정산원장을_연결한다() throws Exception {
        TestFixture fixture = transactionTemplate.execute(status -> {
            Mentor mentor = persistMentor("batch-pending-mentor@example.com");
            Tutorial tutorial = persistTutorial(mentor);
            User mentee = persistUser("batch-pending-mentee@example.com", "batchPendingMentee");
            persistPaymentDetail(tutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 5, 12, 0));
            entityManager.flush();
            return new TestFixture(mentor.getId(), tutorial.getId(), mentee.getId());
        });

        runSettlementJob("2026-07", 1L);
        transactionTemplate.executeWithoutResult(status -> {
            persistPaymentDetail(fixture.tutorialId(), fixture.menteeId(), 2, 2000, LocalDateTime.of(2026, 7, 10, 12, 0));
            entityManager.flush();
        });

        runSettlementJob("2026-07", 2L);

        transactionTemplate.executeWithoutResult(status -> {
            Settlement settlement = findSettlement(fixture.mentorId(), YearMonth.of(2026, 7));
            List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdOrderByCreatedAtDesc(settlement.getId());
            List<SettlementTarget> targets = settlementTargetRepository.findBySettlementPeriod("2026-07");

            assertThat(settlement.getTotalPaymentAmount()).isEqualTo(30000);
            assertThat(settlement.getPlatformFee()).isEqualTo(3000);
            assertThat(settlement.getSettlementAmount()).isEqualTo(27000);
            assertThat(details).hasSize(2);
            assertThat(targets)
                    .extracting(SettlementTarget::getStatus)
                    .containsOnly(SettlementTarget.SettlementTargetStatus.DONE);
        });
    }

    @Test
    void 지급완료된_정산에_같은월_미정산원장이_생기면_정산은_수정하지_않고_target_FAILED로_남긴다() throws Exception {
        TestFixture fixture = transactionTemplate.execute(status -> {
            Mentor mentor = persistMentor("batch-completed-mentor@example.com");
            Tutorial tutorial = persistTutorial(mentor);
            User mentee = persistUser("batch-completed-mentee@example.com", "batchCompletedMentee");
            persistPaymentDetail(tutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 5, 12, 0));
            entityManager.flush();
            return new TestFixture(mentor.getId(), tutorial.getId(), mentee.getId());
        });

        runSettlementJob("2026-07", 3L);
        transactionTemplate.executeWithoutResult(status -> {
            Settlement completedSettlement = findSettlement(fixture.mentorId(), YearMonth.of(2026, 7));
            completedSettlement.complete();
            persistPaymentDetail(fixture.tutorialId(), fixture.menteeId(), 2, 2000, LocalDateTime.of(2026, 7, 10, 12, 0));
            entityManager.flush();
        });

        runSettlementJob("2026-07", 4L);

        transactionTemplate.executeWithoutResult(status -> {
            Settlement settlement = findSettlement(fixture.mentorId(), YearMonth.of(2026, 7));
            List<SettlementDetail> linkedDetails = settlementDetailRepository.findBySettlementIdOrderByCreatedAtDesc(settlement.getId());
            List<SettlementTarget> targets = settlementTargetRepository.findBySettlementPeriod("2026-07");

            assertThat(settlement.getTotalPaymentAmount()).isEqualTo(10000);
            assertThat(settlement.getPlatformFee()).isEqualTo(1000);
            assertThat(settlement.getSettlementAmount()).isEqualTo(9000);
            assertThat(linkedDetails).hasSize(1);
            assertThat(targets)
                    .extracting(SettlementTarget::getStatus)
                    .contains(SettlementTarget.SettlementTargetStatus.FAILED);
        });
    }

    @Test
    void 같은_정산월_redisson_락을_획득하지_못하면_snapshot_전에_Job이_실패한다() throws Exception {
        transactionTemplate.executeWithoutResult(status -> {
            Mentor mentor = persistMentor("batch-locked-mentor@example.com");
            Tutorial tutorial = persistTutorial(mentor);
            User mentee = persistUser("batch-locked-mentee@example.com", "batchLockedMentee");
            persistPaymentDetail(tutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 5, 12, 0));
            entityManager.flush();
        });
        when(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)).thenReturn(false);

        JobExecution jobExecution = launchSettlementJob("2026-07", 5L);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
        assertThat(settlementTargetRepository.findBySettlementPeriod("2026-07")).isEmpty();
    }

    @Test
    void redisson_오류가_발생하면_skip하지_않고_snapshot_전에_Job이_실패한다() throws Exception {
        transactionTemplate.executeWithoutResult(status -> {
            Mentor mentor = persistMentor("batch-redis-error-mentor@example.com");
            Tutorial tutorial = persistTutorial(mentor);
            User mentee = persistUser("batch-redis-error-mentee@example.com", "batchRedisErrorMentee");
            persistPaymentDetail(tutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 5, 12, 0));
            entityManager.flush();
        });
        when(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)).thenThrow(new IllegalStateException("redis command failed"));

        JobExecution jobExecution = launchSettlementJob("2026-07", 6L);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
        assertThat(settlementTargetRepository.findBySettlementPeriod("2026-07")).isEmpty();
    }

    private void persistPaymentDetail(
            Long tutorialId,
            Long menteeId,
            int count,
            int platformFee,
            LocalDateTime occurredAt
    ) {
        Tutorial tutorial = entityManager.getReference(Tutorial.class, tutorialId);
        User mentee = entityManager.getReference(User.class, menteeId);
        persistPaymentDetail(tutorial, mentee, count, platformFee, occurredAt);
    }

    private Settlement findSettlement(Long mentorId, YearMonth settlementPeriod) {
        Long settlementId = jdbcTemplate.queryForObject(
                "SELECT id FROM settlements WHERE mentor_id = ? AND settlement_period = ?",
                Long.class,
                mentorId,
                settlementPeriod.toString()
        );

        return settlementRepository.findById(settlementId)
                .orElseThrow();
    }

    private void runSettlementJob(String settlementPeriod, long runId) throws Exception {
        JobExecution jobExecution = launchSettlementJob(settlementPeriod, runId);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
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

    private User persistUser(String email, String nickname) {
        User user = User.create(email, "password", nickname, UserRole.MENTEE);
        entityManager.persist(user);
        return user;
    }

    private Mentor persistMentor(String email) {
        User user = User.create(email, "password", email, UserRole.MENTOR);
        entityManager.persist(user);
        Mentor mentor = new Mentor(user, "career");
        entityManager.persist(mentor);
        return mentor;
    }

    private Tutorial persistTutorial(Mentor mentor) {
        Tutorial tutorial = Tutorial.create(mentor, "title", "description", 10000, 60);
        entityManager.persist(tutorial);
        return tutorial;
    }

    private SettlementDetail persistPaymentDetail(
            Tutorial tutorial,
            User mentee,
            int count,
            int platformFee,
            LocalDateTime occurredAt
    ) {
        Payment payment = Payment.create(tutorial, mentee, count);
        entityManager.persist(payment);
        SettlementDetail detail = SettlementDetail.createPayment(payment, platformFee, occurredAt);
        entityManager.persist(detail);
        return detail;
    }

    private record TestFixture(Long mentorId, Long tutorialId, Long menteeId) {
    }
}
