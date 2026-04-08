package com.example.lionproject2backend.settlement.util;

import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import com.example.lionproject2backend.settlement.service.SettlementPeriodLockService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SettlementJobListenerTest {

    @Test
    void Job_완료시_정산기간의_target_상태를_조회한다() {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        SettlementTargetRepository settlementTargetRepository = mock(SettlementTargetRepository.class);
        SettlementPeriodLockService settlementPeriodLockService = mock(SettlementPeriodLockService.class);
        SettlementJobListener listener = new SettlementJobListener(
                eventPublisher,
                settlementTargetRepository,
                settlementPeriodLockService
        );
        JobExecution jobExecution = createJobExecution(BatchStatus.COMPLETED);
        when(settlementTargetRepository.findBySettlementPeriod("2026-07")).thenReturn(List.of());
        when(settlementTargetRepository.findByJobInstanceId(10L)).thenReturn(List.of());

        listener.afterJob(jobExecution);

        verify(settlementTargetRepository).findBySettlementPeriod("2026-07");
        verify(settlementTargetRepository).findByJobInstanceId(10L);
        verify(settlementPeriodLockService).release("2026-07", 20L);
    }

    @Test
    void Job_실패시에도_정산기간의_target_상태를_조회한다() {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        SettlementTargetRepository settlementTargetRepository = mock(SettlementTargetRepository.class);
        SettlementPeriodLockService settlementPeriodLockService = mock(SettlementPeriodLockService.class);
        SettlementJobListener listener = new SettlementJobListener(
                eventPublisher,
                settlementTargetRepository,
                settlementPeriodLockService
        );
        JobExecution jobExecution = createJobExecution(BatchStatus.FAILED);
        when(settlementTargetRepository.findBySettlementPeriod("2026-07")).thenReturn(List.of());
        when(settlementTargetRepository.findByJobInstanceId(10L)).thenReturn(List.of());

        listener.afterJob(jobExecution);

        verify(settlementTargetRepository).findBySettlementPeriod("2026-07");
        verify(settlementTargetRepository).findByJobInstanceId(10L);
        verify(settlementPeriodLockService).release("2026-07", 20L);
    }

    private JobExecution createJobExecution(BatchStatus status) {
        JobExecution jobExecution = new JobExecution(
                new JobInstance(10L, "settlementJob"),
                new JobParametersBuilder()
                        .addString("settlementPeriod", "2026-07")
                        .toJobParameters()
        );
        jobExecution.setId(20L);
        jobExecution.setStatus(status);
        return jobExecution;
    }
}
