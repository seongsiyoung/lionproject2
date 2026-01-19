package com.example.lionproject2backend.settlement.repository;

import static com.example.lionproject2backend.mentor.domain.QMentor.mentor;
import static com.example.lionproject2backend.payment.domain.QPayment.payment;
import static com.example.lionproject2backend.settlement.domain.QSettlementDetail.settlementDetail;
import static com.example.lionproject2backend.tutorial.domain.QTutorial.tutorial;

import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettlementDetailCustomRepositoryImpl implements SettlementDetailCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SettlementDetail> findUnsettledDetailsByPeriod(
            YearMonth settlementPeriod
    ) {
        LocalDateTime startAt = settlementPeriod.atDay(1).atStartOfDay();
        LocalDateTime endAt = settlementPeriod.plusMonths(1).atDay(1).atStartOfDay();

        return queryFactory
                .selectFrom(settlementDetail)
                .join(settlementDetail.payment, payment).fetchJoin()
                .join(payment.tutorial, tutorial).fetchJoin()
                .join(tutorial.mentor, mentor).fetchJoin()
                .where(
                        settlementDetail.settlement.isNull(),
                        payment.paidAt.goe(startAt),
                        payment.paidAt.lt(endAt)
                )
                .fetch();
    }
}

