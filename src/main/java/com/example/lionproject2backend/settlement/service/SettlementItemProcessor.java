package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 스냅샷된 SettlementTarget을 받아 실시간 정산 집계 데이터를 조회한 뒤 Settlement 엔티티로 변환합니다.
 */
@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class SettlementItemProcessor implements ItemProcessor<SettlementTarget, Settlement> {

    private final MentorRepository mentorRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;

    @Value("#{jobParameters['settlementPeriod']}")
    private String settlementPeriodStr;

    @Override
    public Settlement process(SettlementTarget target) {
        YearMonth settlementPeriod = YearMonth.parse(settlementPeriodStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDateTime startAt = settlementPeriod.atDay(1).atStartOfDay();
        LocalDateTime endAt = settlementPeriod.plusMonths(1).atDay(1).atStartOfDay();

        // 1. 해당 멘토의 현재 정산 대상 원장들을 집계 (N+1이 발생하지만, 스냅샷으로 페이징 시프트를 방지함)
        SettlementAggregationRow row = settlementDetailRepository
                .findSettlementAggregationByMentorAndPeriod(target.getMentorId(), startAt, endAt)
                .orElse(null);

        if (row == null || row.getSettlementAmount() == null) {
            return null; // 정산할 데이터가 없으면 스킵
        }

        // 2. 멘토 정보 및 이월액 계산
        Mentor mentor = mentorRepository.getReferenceById(target.getMentorId());
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
