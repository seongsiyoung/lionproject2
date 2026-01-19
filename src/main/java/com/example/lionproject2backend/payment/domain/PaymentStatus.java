package com.example.lionproject2backend.payment.domain;

public enum PaymentStatus {
    PENDING,
    PAID,
    CANCELLED,
    REFUND_REQUESTED,    // 환불 승인 대기 중
    REFUNDED             // 환불 완료
}