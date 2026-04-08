package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * 생성된 Settlement 청크를 저장하고, SettlementDetail 레코드들에 Bulk Update를 수행합니다.
 * 또한, 스냅샷 테이블의 상태를 DONE으로 변경합니다.
 */
@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class SettlementItemWriter implements ItemWriter<Settlement> {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final SettlementTargetRepository settlementTargetRepository;

    @Value("#{stepExecution.jobExecution.jobInstance.id}")
    private Long jobInstanceId;

    @Override
    public void write(Chunk<? extends Settlement> chunk) {
        for (Settlement candidate : chunk.getItems()) {
            Settlement settlement = settlementRepository
                    .findByMentorAndSettlementPeriod(candidate.getMentor(), candidate.getSettlementPeriod())
                    .map(existing -> reuseExistingSettlement(existing, candidate))
                    .orElseGet(() -> settlementRepository.save(candidate));

            if (settlement.getStatus() == SettlementStatus.COMPLETED) {
                markTargetFailed(settlement);
                log.warn(
                        "지급 완료 정산 재처리 차단 - settlementId={}, mentorId={}, period={}",
                        settlement.getId(),
                        settlement.getMentor().getId(),
                        settlement.getSettlementPeriod()
                );
                continue;
            }

            YearMonth period = settlement.getSettlementPeriod();
            LocalDateTime startAt = period.atDay(1).atStartOfDay();
            LocalDateTime endAt = period.plusMonths(1).atDay(1).atStartOfDay();

            int updatedCount = settlementDetailRepository.bulkUpdateSettlementId(
                    settlement.getId(),
                    settlement.getMentor().getId(),
                    startAt,
                    endAt
            );

            settlementTargetRepository.findByJobInstanceIdAndMentorId(jobInstanceId, settlement.getMentor().getId())
                    .ifPresent(target -> {
                        target.markAsDone();
                        settlementTargetRepository.save(target);
                    });

            log.debug("정산(ID: {}) -> 원장 업데이트: {}건, 스냅샷 완료 처리 완료", settlement.getId(), updatedCount);
        }
    }

    private Settlement reuseExistingSettlement(Settlement existing, Settlement candidate) {
        if (existing.getStatus() == SettlementStatus.COMPLETED) {
            return existing;
        }

        YearMonth period = existing.getSettlementPeriod();
        LocalDateTime startAt = period.atDay(1).atStartOfDay();
        LocalDateTime endAt = period.plusMonths(1).atDay(1).atStartOfDay();
        SettlementAggregationRow row = settlementDetailRepository.findSettlementAggregationForRecalculation(
                existing.getMentor().getId(),
                existing.getId(),
                startAt,
                endAt
        ).orElseThrow(() -> new IllegalStateException("정산 재계산 대상 원장을 찾을 수 없습니다."));

        long adjustedNet = row.getSettlementAmount() - candidate.getPreviousCarryOverAmount();
        int payableAmount = (int) Math.max(0, adjustedNet);
        int carryOverAmount = (int) Math.max(0, -adjustedNet);

        existing.recalculate(
                row.getTotalPaymentAmount().intValue(),
                row.getPlatformFee().intValue(),
                row.getSettlementAmount().intValue(),
                row.getRefundAmount().intValue(),
                candidate.getPreviousCarryOverAmount(),
                payableAmount,
                carryOverAmount
        );
        return existing;
    }

    private void markTargetFailed(Settlement settlement) {
        settlementTargetRepository.findByJobInstanceIdAndMentorId(jobInstanceId, settlement.getMentor().getId())
                .ifPresent(target -> {
                    target.markAsFailed();
                    settlementTargetRepository.save(target);
                });
    }
}
