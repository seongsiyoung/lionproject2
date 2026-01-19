package com.example.lionproject2backend.settlement.repository;

import static com.example.lionproject2backend.settlement.domain.QSettlementAdjustment.settlementAdjustment;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.settlement.domain.AdjustmentStatus;
import com.example.lionproject2backend.settlement.domain.SettlementAdjustment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettlementAdjustmentCustomRepositoryImpl implements SettlementAdjustmentCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<SettlementAdjustment> findApplicableAdjustments(
            Mentor mentor,
            YearMonth settlementPeriod
    ) {
        return queryFactory
                .selectFrom(settlementAdjustment)
                .where(
                        settlementAdjustment.mentor.eq(mentor),
                        settlementAdjustment.targetPeriod.loe(settlementPeriod),
                        settlementAdjustment.status.ne(AdjustmentStatus.APPLIED)
                )
                .orderBy(
                        settlementAdjustment.occurredAt.asc()
                )
                .fetch();
    }

}
