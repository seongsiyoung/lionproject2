package com.example.lionproject2backend.settlement.config;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import com.example.lionproject2backend.settlement.service.SettlementItemProcessor;
import com.example.lionproject2backend.settlement.service.SettlementItemWriter;
import com.example.lionproject2backend.settlement.service.SettlementSnapshotTasklet;
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

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class SettlementCreateJobConfig {

    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementSnapshotTasklet settlementSnapshotTasklet;
    private final SettlementTargetRepository settlementTargetRepository;
    private final SettlementItemProcessor settlementItemProcessor;
    private final SettlementItemWriter settlementItemWriter;

    @Bean
    public Job settlementJob(JobExecutionListener settlementJobListener) {
        return new JobBuilder("settlementJob", jobRepository)
                .listener(settlementJobListener)
                // 1단계: 정산 대상 스냅샷 고정 -> 2단계: 고정된 대상 기반 정산 처리
                .start(settlementSnapshotStep())
                .next(settlementChunkStep())
                .build();
    }

    /**
     * Step 1: 대상을 고정하는 스냅샷 단계
     */
    @Bean
    public Step settlementSnapshotStep() {
        return new StepBuilder("settlementSnapshotStep", jobRepository)
                .tasklet(settlementSnapshotTasklet, transactionManager)
                .build();
    }

    /**
     * Step 2: 고정된 대상을 읽어 처리하는 청크 단계
     */
    @Bean
    public Step settlementChunkStep() {
        return new StepBuilder("settlementChunkStep", jobRepository)
                .<SettlementTarget, Settlement>chunk(CHUNK_SIZE, transactionManager)
                .reader(settlementTargetReader(null))
                .processor(settlementItemProcessor)
                .writer(settlementItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<SettlementTarget> settlementTargetReader(
            @Value("#{stepExecution.jobExecution.jobInstance.id}") Long jobInstanceId) {

        return new RepositoryItemReaderBuilder<SettlementTarget>()
                .name("settlementTargetReader")
                .repository(settlementTargetRepository)
                .methodName("findReadyTargetsByJobInstanceId")
                .arguments(jobInstanceId)
                .pageSize(5000) // 스냅샷 테이블에서도 상태 변경(READY -> DONE)에 의한 시프트를 방지하기 위해 넉넉하게 설정
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}
