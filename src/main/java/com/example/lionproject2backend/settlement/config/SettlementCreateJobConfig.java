package com.example.lionproject2backend.settlement.config;

import com.example.lionproject2backend.settlement.service.SettlementCreateTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SettlementCreateJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementCreateTasklet settlementCreateTasklet;


    @Bean
    public Job settlementJob(JobExecutionListener settlementJobListener) {
        return new JobBuilder("settlementJob", jobRepository)
                .listener(settlementJobListener)
                .start(settlementStep())
                .build();
    }

    @Bean
    public Step settlementStep() {
        return new StepBuilder("settlementStep", jobRepository)
                .tasklet(settlementCreateTasklet, transactionManager)
                .build();
    }
}
