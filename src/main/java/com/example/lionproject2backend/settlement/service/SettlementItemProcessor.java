package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * DTO(SettlementAggregationRow)를 받아 Settlement 엔티티로 변환합니다.
 * - `SUM` 연산은 Reader(DB 레벨)에서 끝났기 때문에 메모리 및 CPU 비용이 저렴합니다.
 * - 단, 전월 이월액 조회를 위해 Mentor 단위로 간단한 select가 발생할 수 있습니다 (인덱스 활용).
 */
@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class SettlementItemProcessor implements ItemProcessor<SettlementAggregationRow, Settlement> {

    private final MentorRepository mentorRepository;
    private final SettlementRepository settlementRepository;

    @Value("#{jobParameters['settlementPeriod']}")
    private String settlementPeriodStr;

    @Override
    public Settlement process(SettlementAggregationRow row) {
        YearMonth settlementPeriod = YearMonth.parse(settlementPeriodStr, DateTimeFormatter.ofPattern("yyyy-MM"));

        // 프록시 객체를 사용하여 추가 쿼리 없이 외래키 관계만 셋업
        Mentor mentor = mentorRepository.getReferenceById(row.getMentorId());

        // 이전 월 누적 이월액 조회 
        int previousCarryOver = getOutstandingCarryOver(mentor, settlementPeriod);

        long adjustedNet = row.getSettlementAmount() - previousCarryOver;
        int payableAmount = (int) Math.max(0, adjustedNet);
        int carryOverAmount = (int) Math.max(0, -adjustedNet);

        return Settlement.create(
                mentor,
                settlementPeriod,
                row.getTotalPaymentAmount().intValue(),
                row.getPlatformFee().intValue(),
                row.getSettlementAmount().intValue(),
                row.getRefundAmount().intValue(),
                previousCarryOver,
                payableAmount,
                carryOverAmount
        );
    }

    private int getOutstandingCarryOver(Mentor mentor, YearMonth currentPeriod) {
        YearMonth previousPeriod = currentPeriod.minusMonths(1);
        return settlementRepository
                .findByMentorAndSettlementPeriod(mentor, previousPeriod)
                .map(Settlement::getCarryOverAmount)
                .orElse(0);
    }
}
