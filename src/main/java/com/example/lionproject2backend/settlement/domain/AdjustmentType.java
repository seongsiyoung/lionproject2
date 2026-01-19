package com.example.lionproject2backend.settlement.domain;

public enum AdjustmentType {
    /**
     * 결제 환불로 인한 차감
     */
    REFUND,

    /**
     * 운영자 수동 조정 (오차 보정, CS 처리 등)
     */
    MANUAL,

    /**
     * 정책 위반, 노쇼 등으로 인한 페널티 차감
     */
    PENALTY
}
