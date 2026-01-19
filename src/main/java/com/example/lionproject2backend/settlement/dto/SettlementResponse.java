package com.example.lionproject2backend.settlement.dto;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Getter
@Builder
public class SettlementResponse {

    private Long settlementId;
    private Long mentorId;
    private String mentorName;
    
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth settlementPeriod;
    private int totalAmount;
    private int platformFee;
    private int settlementAmount;
    private int finalSettlementAmount;
    private SettlementStatus status;
    private LocalDateTime settledAt;
    private LocalDateTime createdAt;

    public static SettlementResponse from(Settlement settlement) {
        return SettlementResponse.builder()
                .settlementId(settlement.getId())
                .mentorId(settlement.getMentor().getId())
                .mentorName(settlement.getMentor().getUser().getNickname())
                .settlementPeriod(settlement.getSettlementPeriod())
                .totalAmount(settlement.getTotalAmount())
                .platformFee(settlement.getPlatformFee())
                .settlementAmount(settlement.getSettlementAmount())
                .finalSettlementAmount(settlement.getFinalSettlementAmount())
                .status(settlement.getStatus())
                .settledAt(settlement.getSettledAt())
                .createdAt(settlement.getCreatedAt())
                .build();
    }
}
