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

        // 1. 기존 데이터 정리 (해당 정산 기간 기준) - 재시도 시 중복 방지
        settlementTargetRepository.deleteBySettlementPeriod(settlementPeriod);

        // 2. 현재 정산이 필요한 멘토 ID들을 조회하여 스냅샷 테이블에 삽입
        YearMonth period = YearMonth.parse(settlementPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDateTime startAt = period.atDay(1).atStartOfDay();
        LocalDateTime endAt = period.plusMonths(1).atDay(1).atStartOfDay();

        String sql = "INSERT INTO settlement_targets (mentor_id, settlement_period, job_instance_id, status, created_at, updated_at) " +
                     "SELECT DISTINCT t.mentor_id, ?, ?, 'READY', NOW(), NOW() " +
                     "FROM settlement_details sd " +
                     "JOIN payments p ON sd.payment_id = p.id " +
                     "JOIN tutorials t ON p.tutorial_id = t.id " +
                     "WHERE sd.settlement_id IS NULL " +
                     "AND sd.occurred_at >= ? " +
                     "AND sd.occurred_at < ?";

        int count = jdbcTemplate.update(sql, settlementPeriod, jobInstanceId, startAt, endAt);
        log.info("정산 대상 스냅샷 생성 완료 - 대상 멘토 수: {}명", count);

        return RepeatStatus.FINISHED;
    }
}
