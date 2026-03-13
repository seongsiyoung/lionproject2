package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * 생성된 Settlement 청크를 저장하고, SettlementDetail 레코드들에 Bulk Update를 수행합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementItemWriter implements ItemWriter<Settlement> {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;

    @Override
    public void write(Chunk<? extends Settlement> chunk) {
        List<? extends Settlement> settlements = chunk.getItems();

        // 1. Settlement 엔티티 저장
        // (IDENTITY 전략 사용 중이므로 실제로는 건당 INSERT 발생)
        settlementRepository.saveAll(settlements);

        // 2. 생성된 Settlement의 ID를 바탕으로 Bulk Update 실행
        for (Settlement s : settlements) {
            YearMonth period = s.getSettlementPeriod();
            LocalDateTime startAt = period.atDay(1).atStartOfDay();
            LocalDateTime endAt = period.plusMonths(1).atDay(1).atStartOfDay();

            int updatedCount = settlementDetailRepository.bulkUpdateSettlementId(
                    s.getId(),
                    s.getMentor().getId(),
                    startAt,
                    endAt
            );
            log.debug("정산(ID: {}) -> 등록된 디테일 벌크 업데이트 건수: {}", s.getId(), updatedCount);
        }
    }
}
