package com.example.lionproject2backend.settlement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 정산 자동 생성 스케줄러
 * 매월 1일 자정에 전월 정산을 자동 생성
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {

    private final JobLauncher jobLauncher;
    private final Job settlementJob;
    private final Job settlementCompleteJob;

    /**
     * 매월 1일 00:00:00에 실행
     * 전월 정산 생성
     */
    @Scheduled(cron = "0 54 19 18 1 ?")
    //@Scheduled(cron = "0 0 0 1 * ?")
    public void createMonthlySettlement() {
        try {
            YearMonth previousMonth = YearMonth.now().minusMonths(1);
            String settlementPeriodStr = previousMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            log.info("자동 정산 생성 시작 - settlementPeriod: {}", settlementPeriodStr);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("settlementPeriod", settlementPeriodStr)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(settlementJob, jobParameters);

            log.info("자동 정산 생성 Job 시작 완료 - settlementPeriod: {}", settlementPeriodStr);

        } catch (Exception e) {
            log.error("자동 정산 생성 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "0 55 19 18 1 ?")
    //@Scheduled(cron = "0 0 2 7 * ?")
    public void completeMonthlySettlements() {
        YearMonth targetPeriod = YearMonth.now().minusMonths(1);

        JobParameters params = new JobParametersBuilder()
                .addString("settlementPeriod", targetPeriod.toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(settlementCompleteJob, params);
        } catch (Exception e) {
            log.error("정산 지급 Batch 실행 실패", e);
        }
    }
}
