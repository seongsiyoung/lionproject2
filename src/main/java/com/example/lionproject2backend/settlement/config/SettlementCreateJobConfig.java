package com.example.lionproject2backend.settlement.config;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.service.SettlementCreateTasklet;
import com.example.lionproject2backend.settlement.service.SettlementItemProcessor;
import com.example.lionproject2backend.settlement.service.SettlementItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class SettlementCreateJobConfig {

    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementCreateTasklet settlementCreateTasklet;
    private final SettlementDetailRepository settlementDetailRepository;
    private final SettlementItemProcessor settlementItemProcessor;
    private final SettlementItemWriter settlementItemWriter;

    @Bean
    public Job settlementJob(JobExecutionListener settlementJobListener) {
        return new JobBuilder("settlementJob", jobRepository)
                .listener(settlementJobListener)
                // 기존 Tasklet 대신 새로운 Chunk 기반 Step을 사용합니다.
                .start(settlementChunkStep())
                // .start(settlementStep()) // 구버전 Tasklet (비활성화됨)
                .build();
    }

    /**
     * 구버전 Tasklet (비활성화됨)
     * 차후 롤백이나 비교를 위해 남겨둡니다.
     */
    @Bean
    public Step settlementStep() {
        return new StepBuilder("settlementStep", jobRepository)
                .tasklet(settlementCreateTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step settlementChunkStep() {
        return new StepBuilder("settlementChunkStep", jobRepository)
                .<SettlementAggregationRow, Settlement>chunk(CHUNK_SIZE, transactionManager)
                .reader(settlementAggregationReader(null))
                .processor(settlementItemProcessor)
                .writer(settlementItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<SettlementAggregationRow> settlementAggregationReader(
            @Value("#{jobParameters['settlementPeriod']}") String settlementPeriodStr) {

        YearMonth period = YearMonth.parse(settlementPeriodStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDateTime startAt = period.atDay(1).atStartOfDay();
        LocalDateTime endAt = period.plusMonths(1).atDay(1).atStartOfDay();

        return new RepositoryItemReaderBuilder<SettlementAggregationRow>()
                .name("settlementAggregationReader")
                .repository(settlementDetailRepository)
                .methodName("findSettlementAggregationByPeriod")
                .arguments(startAt, endAt) // Pageable은 내장 빌더가 자동 주입
                .pageSize(CHUNK_SIZE)
                .sorts(Collections.singletonMap("payment.tutorial.mentor.id", Sort.Direction.ASC))
                .build();
    }
}
