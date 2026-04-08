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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("e2e")
@Testcontainers
@Sql(
        scripts = "classpath:org/springframework/batch/core/schema-mysql.sql",
        config = @SqlConfig(errorMode = SqlConfig.ErrorMode.CONTINUE_ON_ERROR)
)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.show-sql=false",
        "spring.sql.init.mode=never",
        "spring.batch.jdbc.initialize-schema=always",
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
class SettlementBatchEndToEndTest {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.4"))
            .withDatabaseName("settlement_e2e")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("redisson.address", () -> "redis://" + redis.getHost() + ":" + redis.getMappedPort(6379));
    }

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

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void 실제_MySQL과_Redis에서_정산_Job이_완료되고_Redisson_락이_해제된다() throws Exception {
        TestFixture fixture = transactionTemplate.execute(status -> {
            Mentor mentor = persistMentor("e2e-mentor@example.com");
            Tutorial tutorial = persistTutorial(mentor);
            User mentee = persistUser("e2e-mentee@example.com", "e2eMentee");
            persistPaymentDetail(tutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 5, 12, 0));
            entityManager.flush();
            return new TestFixture(mentor.getId(), tutorial.getId(), mentee.getId());
        });

        JobExecution firstExecution = launchSettlementJob("2026-07", 1001L);
        transactionTemplate.executeWithoutResult(status -> {
            persistPaymentDetail(fixture.tutorialId(), fixture.menteeId(), 2, 2000, LocalDateTime.of(2026, 7, 10, 12, 0));
            entityManager.flush();
        });

        JobExecution secondExecution = launchSettlementJob("2026-07", 1002L);

        assertThat(firstExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(secondExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(redissonClient.getLock("settlement:create:2026-07").isLocked()).isFalse();

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
    void 실제_Redis에서_같은_정산월_락이_이미_잡혀있으면_snapshot_전에_Job이_실패한다() throws Exception {
        transactionTemplate.executeWithoutResult(status -> {
            Mentor mentor = persistMentor("e2e-locked-mentor@example.com");
            Tutorial tutorial = persistTutorial(mentor);
            User mentee = persistUser("e2e-locked-mentee@example.com", "e2eLockedMentee");
            persistPaymentDetail(tutorial, mentee, 1, 1000, LocalDateTime.of(2026, 8, 5, 12, 0));
            entityManager.flush();
        });

        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            RLock lock = redissonClient.getLock("settlement:create:2026-08");
            lock.lock();
            locked.countDown();
            try {
                release.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });

        assertThat(locked.await(10, TimeUnit.SECONDS)).isTrue();

        try {
            JobExecution jobExecution = launchSettlementJob("2026-08", 1003L);

            assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
            assertThat(settlementTargetRepository.findBySettlementPeriod("2026-08")).isEmpty();
        } finally {
            release.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        }
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
