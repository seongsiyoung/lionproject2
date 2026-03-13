package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.settlement.domain.Settlement;
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
import java.util.List;

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
        List<? extends Settlement> settlements = chunk.getItems();

        // 1. Settlement 엔티티 저장
        settlementRepository.saveAll(settlements);

        // 2. 원장 벌크 업데이트 및 스냅샷 상태 업데이트
        for (Settlement s : settlements) {
            YearMonth period = s.getSettlementPeriod();
            LocalDateTime startAt = period.atDay(1).atStartOfDay();
            LocalDateTime endAt = period.plusMonths(1).atDay(1).atStartOfDay();

            // 2-1. 원장 Bulk Update
            int updatedCount = settlementDetailRepository.bulkUpdateSettlementId(
                    s.getId(),
                    s.getMentor().getId(),
                    startAt,
                    endAt
            );
            
            // 2-2. 스냅샷 상태 완료(DONE) 처리
            settlementTargetRepository.findByJobInstanceIdAndMentorId(jobInstanceId, s.getMentor().getId())
                    .ifPresent(target -> {
                        target.markAsDone();
                        settlementTargetRepository.save(target);
                    });

            log.debug("정산(ID: {}) -> 원장 업데이트: {}건, 스냅샷 완료 처리 완료", s.getId(), updatedCount);
        }
    }
}
