package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 정산 시작 시점에 처리 대상(멘토 목록)을 JobInstanceId와 함께 스냅샷으로 고정하는 Tasklet입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementSnapshotTasklet implements Tasklet {

    private final SettlementTargetRepository settlementTargetRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String settlementPeriod = (String) chunkContext.getStepContext().getJobParameters().get("settlementPeriod");
        Long jobInstanceId = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
        
        log.info("정산 대상 스냅샷 생성 시작 - settlementPeriod: {}, jobInstanceId: {}", settlementPeriod, jobInstanceId);

        YearMonth period = YearMonth.parse(settlementPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDateTime startAt = period.atDay(1).atStartOfDay();
        LocalDateTime endAt = period.plusMonths(1).atDay(1).atStartOfDay();

        int resetCount = settlementTargetRepository.resetRetryableTargets(settlementPeriod, jobInstanceId);
        int reopenedCount = settlementTargetRepository.reopenPendingDoneTargets(settlementPeriod, jobInstanceId, startAt, endAt);
        int failedCount = settlementTargetRepository.markCompletedDoneTargetsFailed(settlementPeriod, jobInstanceId, startAt, endAt);

        String sql = "INSERT INTO settlement_targets (mentor_id, settlement_period, job_instance_id, status, created_at, updated_at) " +
                     "SELECT DISTINCT t.mentor_id, ?, ?, 'READY', NOW(), NOW() " +
                     "FROM settlement_details sd " +
                     "JOIN payments p ON sd.payment_id = p.id " +
                     "JOIN tutorials t ON p.tutorial_id = t.id " +
                     "WHERE sd.settlement_id IS NULL " +
                     "AND sd.occurred_at >= ? " +
                     "AND sd.occurred_at < ? " +
                     "AND NOT EXISTS ( " +
                     "    SELECT 1 " +
                     "    FROM settlement_targets st " +
                     "    WHERE st.mentor_id = t.mentor_id " +
                     "    AND st.settlement_period = ? " +
                     ")";

        int count = jdbcTemplate.update(sql, settlementPeriod, jobInstanceId, startAt, endAt, settlementPeriod);
        log.info(
                "정산 대상 스냅샷 생성 완료 - 복구 대상: {}건, 재처리 대상: {}건, 지급완료 충돌: {}건, 신규 대상 멘토 수: {}명",
                resetCount,
                reopenedCount,
                failedCount,
                count
        );

        return RepeatStatus.FINISHED;
    }
}
