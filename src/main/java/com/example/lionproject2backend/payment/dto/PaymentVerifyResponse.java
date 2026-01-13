package com.example.lionproject2backend.payment.dto;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import com.example.lionproject2backend.ticket.domain.Ticket;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentVerifyResponse {

    private Long paymentId;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private Long ticketId;
    private int totalCount;
    private int remainingCount;
    private LocalDateTime expiredAt;

    public static PaymentVerifyResponse from(Payment payment, Ticket ticket) {
        return PaymentVerifyResponse.builder()
                .paymentId(payment.getId())
                .paymentStatus(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .ticketId(ticket.getId())
                .totalCount(ticket.getTotalCount())
                .remainingCount(ticket.getRemainingCount())
                .expiredAt(ticket.getExpiredAt())
                .build();
    }
}
