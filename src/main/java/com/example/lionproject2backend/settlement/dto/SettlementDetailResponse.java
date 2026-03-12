package com.example.lionproject2backend.settlement.dto;

import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementDetailType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SettlementDetailResponse {

    private Long paymentId;
    private Long tutorialId;
    private String tutorialTitle;
    private SettlementDetailType type;
    private int paymentAmount;
    private int platformFee;
    private int settlementAmount;
    private LocalDateTime occurredAt;

    public static SettlementDetailResponse from(SettlementDetail detail) {
        return SettlementDetailResponse.builder()
                .paymentId(detail.getPayment().getId())
                .tutorialId(detail.getPayment().getTutorial().getId())
                .tutorialTitle(detail.getPayment().getTutorial().getTitle())
                .type(detail.getType())
                .paymentAmount(detail.getPaymentAmount())
                .platformFee(detail.getPlatformFee())
                .settlementAmount(detail.getSettlementAmount())
                .occurredAt(detail.getOccurredAt())
                .build();
    }
}
