package com.example.lionproject2backend.settlement.util;

import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.event.SettlementFailedEvent;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import com.example.lionproject2backend.settlement.service.SettlementPeriodLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettlementJobListener implements JobExecutionListener {

    private final ApplicationEventPublisher eventPublisher;
    private final SettlementTargetRepository settlementTargetRepository;
    private final SettlementPeriodLockService settlementPeriodLockService;
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
        String settlementPeriod = jobExecution.getJobParameters().getString("settlementPeriod", "UNKNOWN");
        settlementPeriodLockService.release(settlementPeriod, jobExecution.getId());
        Map<SettlementTarget.SettlementTargetStatus, Long> currentTargetCounts = countCurrentTargets(jobExecution.getJobInstance().getInstanceId());
        Map<SettlementTarget.SettlementTargetStatus, Long> periodTargetCounts = countPeriodTargets(settlementPeriod);

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info(
                    "정산 Job 완료 - jobId={}, period={}, status={}, currentTargetCounts={}, periodTargetCounts={}",
                    jobExecution.getId(),
                    settlementPeriod,
                    jobExecution.getStatus(),
                    currentTargetCounts,
                    periodTargetCounts
            );
            warnIfSkippedOrFailed(currentTargetCounts);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            String exitMessage = jobExecution.getExitStatus().getExitDescription();
            
            log.error(
                    "정산 Job 실패 - jobId={}, status={}, period={}, currentTargetCounts={}, periodTargetCounts={}, exit={}",
                    jobExecution.getId(),
                    jobExecution.getStatus(),
                    settlementPeriod,
                    currentTargetCounts,
                    periodTargetCounts,
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

    private Map<SettlementTarget.SettlementTargetStatus, Long> countCurrentTargets(Long jobInstanceId) {
        return settlementTargetRepository.findByJobInstanceId(jobInstanceId)
                .stream()
                .collect(Collectors.groupingBy(SettlementTarget::getStatus, Collectors.counting()));
    }

    private Map<SettlementTarget.SettlementTargetStatus, Long> countPeriodTargets(String settlementPeriod) {
        return settlementTargetRepository.findBySettlementPeriod(settlementPeriod)
                .stream()
                .collect(Collectors.groupingBy(SettlementTarget::getStatus, Collectors.counting()));
    }

    private void warnIfSkippedOrFailed(Map<SettlementTarget.SettlementTargetStatus, Long> targetCounts) {
        long skippedCount = targetCounts.getOrDefault(SettlementTarget.SettlementTargetStatus.SKIPPED, 0L);
        long failedCount = targetCounts.getOrDefault(SettlementTarget.SettlementTargetStatus.FAILED, 0L);

        if (skippedCount > 0 || failedCount > 0) {
            log.warn("정산 Job 대상 일부 미처리 - skippedCount={}, failedCount={}", skippedCount, failedCount);
        }
    }
}
