package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SettlementSnapshotTaskletTest {

    @Mock
    private SettlementTargetRepository settlementTargetRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void 스냅샷_생성시_기간_기준_전체삭제를_호출하지_않고_복구와_신규삽입을_수행한다() throws Exception {
        SettlementSnapshotTasklet tasklet = new SettlementSnapshotTasklet(settlementTargetRepository, jdbcTemplate);
        ChunkContext chunkContext = createChunkContext("2026-07", 10L);

        RepeatStatus status = tasklet.execute(mock(StepContribution.class), chunkContext);

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(settlementTargetRepository, never()).deleteBySettlementPeriod("2026-07");
        verify(settlementTargetRepository).resetRetryableTargets("2026-07", 10L);
        verify(settlementTargetRepository).reopenPendingDoneTargets(
                eq("2026-07"),
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        verify(settlementTargetRepository).markCompletedDoneTargetsFailed(
                eq("2026-07"),
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(
                sqlCaptor.capture(),
                eq("2026-07"),
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq("2026-07")
        );
        assertThat(sqlCaptor.getValue()).contains("NOT EXISTS");
    }

    private ChunkContext createChunkContext(String settlementPeriod, Long jobInstanceId) {
        JobInstance jobInstance = new JobInstance(jobInstanceId, "settlementJob");
        JobExecution jobExecution = new JobExecution(
                jobInstance,
                new JobParametersBuilder()
                        .addString("settlementPeriod", settlementPeriod)
                        .toJobParameters()
        );
        StepExecution stepExecution = new StepExecution("settlementSnapshotStep", jobExecution);
        return new ChunkContext(new StepContext(stepExecution));
    }
}
