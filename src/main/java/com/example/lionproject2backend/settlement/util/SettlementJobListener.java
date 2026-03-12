package com.example.lionproject2backend.settlement.util;

import com.example.lionproject2backend.settlement.event.SettlementFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettlementJobListener implements JobExecutionListener {

    private final ApplicationEventPublisher eventPublisher;
    @Override

    public void beforeJob(JobExecution jobExecution) {
        log.info(
                "정산 Job 시작 - jobId={}, parameters={}",
                jobExecution.getId(),
                jobExecution.getJobParameters()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info(
                    "정산 Job 완료 - jobId={}, status={}",
                    jobExecution.getId(),
                    jobExecution.getStatus()
            );
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            String settlementPeriod = jobExecution.getJobParameters().getString("settlementPeriod", "UNKNOWN");
            String exitMessage = jobExecution.getExitStatus().getExitDescription();
            
            log.error(
                    "정산 Job 실패 - jobId={}, status={}, period={}, exit={}",
                    jobExecution.getId(),
                    jobExecution.getStatus(),
                    settlementPeriod,
                    exitMessage
            );

            eventPublisher.publishEvent(new SettlementFailedEvent(
                    jobExecution.getId(),
                    "settlementStep", // 기본 스텝명 또는 더 정교하게 추출 가능
                    exitMessage,
                    settlementPeriod
            ));
        }
    }
}
