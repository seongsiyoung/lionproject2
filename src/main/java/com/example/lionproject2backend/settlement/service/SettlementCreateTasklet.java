package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.settlement.dto.SettlementResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 정산 생성 Tasklet
 * Spring Batch Step에서 실행되는 실제 정산 로직
 * SettlementService.createSettlements()를 호출하여 정산 생성
 * 실패 정보를 ExecutionContext에 저장하여 추적 가능
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementCreateTasklet implements Tasklet {

    private final SettlementService settlementService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        String settlementPeriodStr = (String) chunkContext.getStepContext()
                .getJobParameters()
                .get("settlementPeriod");

        YearMonth settlementPeriod = YearMonth.parse(settlementPeriodStr, DateTimeFormatter.ofPattern("yyyy-MM"));

        log.info("정산 생성 Tasklet 시작 - settlementPeriod: {}", settlementPeriodStr);

        List<SettlementResponse> settlements =
                settlementService.createSettlements(settlementPeriod);

        int createdCount = settlements.size();

        contribution.incrementWriteCount(createdCount);

        log.info(
                "정산 생성 Tasklet 완료 - settlementPeriod={}, 생성된 정산 수={}",
                settlementPeriodStr, createdCount
        );

        return RepeatStatus.FINISHED;
    }
}
