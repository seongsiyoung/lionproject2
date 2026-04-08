package com.example.lionproject2backend.settlement.exception;

import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import lombok.Getter;

@Getter
public class SettlementTargetSkippableException extends RuntimeException {

    private final SettlementTarget target;

    public SettlementTargetSkippableException(SettlementTarget target, String message) {
        super(message);
        this.target = target;
    }
}
