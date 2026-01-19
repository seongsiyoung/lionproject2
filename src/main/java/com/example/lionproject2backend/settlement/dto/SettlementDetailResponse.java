package com.example.lionproject2backend.settlement.dto;

import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SettlementDetailResponse {

    private Long paymentId;
    private Long tutorialId;
    private String tutorialTitle;
    private int paymentAmount;
    private int platformFee;
    private int settlementAmount;
    private LocalDateTime paidAt;

    public static SettlementDetailResponse from(SettlementDetail detail) {
        return SettlementDetailResponse.builder()
                .paymentId(detail.getPayment().getId())
                .tutorialId(detail.getPayment().getTutorial().getId())
                .tutorialTitle(detail.getPayment().getTutorial().getTitle())
                .paymentAmount(detail.getPaymentAmount())
                .platformFee(detail.getPlatformFee())
                .settlementAmount(detail.getSettlementAmount())
                .paidAt(detail.getPayment().getPaidAt())
                .build();
    }
}
