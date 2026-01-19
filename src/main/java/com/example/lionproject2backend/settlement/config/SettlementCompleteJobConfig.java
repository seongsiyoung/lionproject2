package com.example.lionproject2backend.settlement.config;

import com.example.lionproject2backend.settlement.service.SettlementCompleteTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SettlementCompleteJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementCompleteTasklet tasklet;

    @Bean
    public Job settlementCompleteJob() {
        return new JobBuilder("settlementCompleteJob", jobRepository)
                .start(settlementCompleteStep())
                .build();
    }

    @Bean
    public Step settlementCompleteStep() {
        return new StepBuilder("settlementCompleteStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
