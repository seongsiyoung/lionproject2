package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SettlementRepositoryIntegrationTest.QuerydslTestConfig.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:settlement_repository_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never"
})
class SettlementRepositoryIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SettlementDetailRepository settlementDetailRepository;

    @Autowired
    private SettlementTargetRepository settlementTargetRepository;

    @Test
    void 기존_정산_연결원장과_미정산원장을_함께_재계산_집계한다() {
        Mentor mentor = persistMentor("mentor@example.com");
        Tutorial tutorial = persistTutorial(mentor);
        User mentee = persistUser("mentee@example.com", "mentee");
        Settlement settlement = persistSettlement(mentor, YearMonth.of(2026, 7));
        SettlementDetail linkedDetail = persistPaymentDetail(tutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 5, 12, 0));
        linkedDetail.assignSettlement(settlement);
        persistPaymentDetail(tutorial, mentee, 2, 2000, LocalDateTime.of(2026, 7, 10, 12, 0));
        entityManager.flush();
        entityManager.clear();

        SettlementAggregationRow row = settlementDetailRepository.findSettlementAggregationForRecalculation(
                mentor.getId(),
                settlement.getId(),
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 8, 1, 0, 0)
        ).orElseThrow();

        assertThat(row.getMentorId()).isEqualTo(mentor.getId());
        assertThat(row.getTotalPaymentAmount()).isEqualTo(30000L);
        assertThat(row.getPlatformFee()).isEqualTo(3000L);
        assertThat(row.getSettlementAmount()).isEqualTo(27000L);
    }

    @Test
    void target_복구와_DONE_target_재오픈_및_COMPLETED_충돌처리를_수행한다() {
        Mentor pendingMentor = persistMentor("pending-mentor@example.com");
        Mentor completedMentor = persistMentor("completed-mentor@example.com");
        Tutorial pendingTutorial = persistTutorial(pendingMentor);
        Tutorial completedTutorial = persistTutorial(completedMentor);
        User mentee = persistUser("target-mentee@example.com", "targetMentee");

        persistSettlement(pendingMentor, YearMonth.of(2026, 7));
        Settlement completedSettlement = persistSettlement(completedMentor, YearMonth.of(2026, 7));
        completedSettlement.complete();

        SettlementTarget ready = SettlementTarget.create(10L, "2026-07", 1L);
        SettlementTarget processing = SettlementTarget.create(11L, "2026-07", 1L);
        processing.markAsProcessing();
        SettlementTarget skipped = SettlementTarget.create(12L, "2026-07", 1L);
        skipped.markAsSkipped();
        SettlementTarget pendingDone = SettlementTarget.create(pendingMentor.getId(), "2026-07", 1L);
        pendingDone.markAsDone();
        SettlementTarget completedDone = SettlementTarget.create(completedMentor.getId(), "2026-07", 1L);
        completedDone.markAsDone();
        entityManager.persist(ready);
        entityManager.persist(processing);
        entityManager.persist(skipped);
        entityManager.persist(pendingDone);
        entityManager.persist(completedDone);

        persistPaymentDetail(pendingTutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 10, 12, 0));
        persistPaymentDetail(completedTutorial, mentee, 1, 1000, LocalDateTime.of(2026, 7, 11, 12, 0));
        entityManager.flush();
        entityManager.clear();

        int resetCount = settlementTargetRepository.resetRetryableTargets("2026-07", 99L);
        int reopenedCount = settlementTargetRepository.reopenPendingDoneTargets(
                "2026-07",
                99L,
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 8, 1, 0, 0)
        );
        int failedCount = settlementTargetRepository.markCompletedDoneTargetsFailed(
                "2026-07",
                99L,
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 8, 1, 0, 0)
        );
        entityManager.flush();
        entityManager.clear();

        assertThat(resetCount).isEqualTo(3);
        assertThat(reopenedCount).isEqualTo(1);
        assertThat(failedCount).isEqualTo(1);
        assertThat(settlementTargetRepository.findByJobInstanceId(99L))
                .extracting(SettlementTarget::getStatus)
                .containsExactlyInAnyOrder(
                        SettlementTarget.SettlementTargetStatus.READY,
                        SettlementTarget.SettlementTargetStatus.READY,
                        SettlementTarget.SettlementTargetStatus.READY,
                        SettlementTarget.SettlementTargetStatus.READY,
                        SettlementTarget.SettlementTargetStatus.FAILED
                );
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

    private Settlement persistSettlement(Mentor mentor, YearMonth period) {
        Settlement settlement = Settlement.create(mentor, period, 10000, 1000, 9000, 0, 0, 9000, 0);
        entityManager.persist(settlement);
        return settlement;
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

    @TestConfiguration
    static class QuerydslTestConfig {

        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }
}
