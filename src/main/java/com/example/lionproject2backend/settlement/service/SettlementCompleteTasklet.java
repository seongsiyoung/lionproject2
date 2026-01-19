package com.example.lionproject2backend.settlement.service;

import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementCompleteTasklet implements Tasklet {

    private final SettlementService settlementService;

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {

        String periodStr = (String) chunkContext
                .getStepContext()
                .getJobParameters()
                .get("settlementPeriod");

        YearMonth period = YearMonth.parse(periodStr);

        log.info("정산 지급 처리 시작 - period={}", period);

        int completedCount =
                settlementService.completeSettlementsByPeriod(period);

        contribution.incrementWriteCount(completedCount);

        log.info("정산 지급 처리 완료 - count={}", completedCount);

        return RepeatStatus.FINISHED;
    }
}
