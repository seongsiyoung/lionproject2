package com.example.lionproject2backend.settlement.event;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SettlementFailedEvent {

    private final Long jobId;
    private final String stepName;
    private final String exitMessage;
    private final String settlementPeriod;
    private final LocalDateTime occurredAt;

    public SettlementFailedEvent(
            Long jobId,
            String stepName,
            String exitMessage,
            String settlementPeriod ) {
        this.jobId = jobId;
        this.stepName = stepName;
        this.exitMessage = exitMessage;
        this.settlementPeriod = settlementPeriod;
        this.occurredAt = LocalDateTime.now();
    }
}


