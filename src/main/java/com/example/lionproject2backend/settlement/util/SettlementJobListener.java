package com.example.lionproject2backend.settlement.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SettlementJobListener implements JobExecutionListener {

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
            log.error(
                    "정산 Job 실패 - jobId={}, status={}, exit={}",
                    jobExecution.getId(),
                    jobExecution.getStatus(),
                    jobExecution.getExitStatus().getExitDescription()
            );
        }
    }
}
