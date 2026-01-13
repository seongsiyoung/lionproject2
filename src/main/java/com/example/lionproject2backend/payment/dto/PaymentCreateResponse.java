package com.example.lionproject2backend.payment.dto;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCreateResponse {

    private Long paymentId;
    private Long tutorialId;
    private String tutorialTitle;
    private int count;
    private int amount;
    private PaymentStatus status;

    public static PaymentCreateResponse from(Payment payment) {
        return PaymentCreateResponse.builder()
                .paymentId(payment.getId())
                .tutorialId(payment.getTutorial().getId())
                .tutorialTitle(payment.getTutorial().getTitle())
                .count(payment.getCount())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
}
