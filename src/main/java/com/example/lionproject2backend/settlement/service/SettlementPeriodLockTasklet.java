package com.example.lionproject2backend.settlement.service;

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
public class SettlementPeriodLockTasklet implements Tasklet {

    private final SettlementPeriodLockService settlementPeriodLockService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        String settlementPeriod = (String) chunkContext.getStepContext().getJobParameters().get("settlementPeriod");
        Long jobExecutionId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();

        settlementPeriodLockService.acquire(settlementPeriod, jobExecutionId);
        log.info("정산 기간 락 획득 완료 - settlementPeriod={}, jobExecutionId={}", settlementPeriod, jobExecutionId);

        return RepeatStatus.FINISHED;
    }
}
