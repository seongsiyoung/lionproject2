package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.util.List;

import static com.example.lionproject2backend.settlement.domain.QSettlement.settlement;

@RequiredArgsConstructor
public class SettlementCustomRepositoryImpl implements SettlementCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Settlement> findSettlements(
            Long mentorId,
            SettlementStatus status,
            YearMonth startPeriod,
            YearMonth endPeriod
    ) {

        return queryFactory
                .selectFrom(settlement)
                .where(
                        mentorEq(mentorId),
                        statusEq(status),
                        periodBetween(startPeriod, endPeriod)
                )
                .orderBy(settlement.settlementPeriod.desc())
                .fetch();
    }

    /**
     * 기간 범위 조건
     * - startPeriod가 null이고 endPeriod가 있으면: 처음부터 endPeriod까지
     * - startPeriod가 있고 endPeriod가 null이면: startPeriod부터 최근까지
     * - 둘 다 있으면: startPeriod부터 endPeriod까지
     * - 둘 다 null이면: 조건 없음 (모든 기간)
     */
    private BooleanExpression mentorEq(Long mentorId) {
        return mentorId != null ? settlement.mentor.id.eq(mentorId) : null;
    }

    private BooleanExpression statusEq(SettlementStatus status) {
        return status != null ? settlement.status.eq(status) : null;
    }

    private BooleanExpression periodBetween(YearMonth startPeriod, YearMonth endPeriod) {
        if (startPeriod == null && endPeriod == null) {
            return null;
        }
        if (startPeriod == null) {
            return settlement.settlementPeriod.loe(endPeriod);
        }
        if (endPeriod == null) {
            return settlement.settlementPeriod.goe(startPeriod);
        }
        return settlement.settlementPeriod.between(startPeriod, endPeriod);
    }
}
