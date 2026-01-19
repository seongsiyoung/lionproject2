package com.example.lionproject2backend.payment.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id", nullable = false)
    private Tutorial tutorial;

    @Column(name = "imp_uid")
    private String impUid;

    @Column(name = "merchant_uid")
    private String merchantUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @Column(nullable = false)
    private int amount;

    @Column(name = "count", nullable = false)
    private int count;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "refunded_amount", nullable = false)
    private Integer refundedAmount = 0;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    // 생성 메서드
    public static Payment create(Tutorial tutorial, User mentee, int count) {
        Payment payment = new Payment();
        payment.tutorial = tutorial;
        payment.mentee = mentee;
        payment.count = count;
        payment.amount = tutorial.getPrice() * count;
        payment.status = PaymentStatus.PENDING;
        return payment;
    }

    // 결제 완료 처리
    public void complete(String impUid, String merchantUid) {
        if (this.status != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.PAYMENT_CANNOT_COMPLETE);
        }
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    // 결제 취소
    public void cancel() {
        if (this.status == PaymentStatus.PAID) {
            throw new CustomException(ErrorCode.PAYMENT_CANNOT_CANCEL);
        }
        this.status = PaymentStatus.CANCELLED;
    }

    /**
     * 환불 신청 (멘티가 환불 요청)
     * 상태만 REFUND_REQUESTED로 변경, 실제 환불은 관리자 승인 후 처리
     */
    public void requestRefund() {
        if (this.status != PaymentStatus.PAID) {
            throw new CustomException(ErrorCode.PAYMENT_CANNOT_REQUEST_REFUND);
        }
        this.status = PaymentStatus.REFUND_REQUESTED;
    }

    /**
     * 환불 거절 (관리자가 환불 신청 거절)
     * 상태를 원래대로 복구 (PAID)
     */
    public void rejectRefund() {
        if (this.status != PaymentStatus.REFUND_REQUESTED) {
            throw new CustomException(ErrorCode.PAYMENT_CANNOT_REJECT_REFUND);
        }
        this.status = PaymentStatus.PAID;
    }

    /**
     * 환불 신청 취소
     * 상태를 원래대로 복구 (PAID)
     */
    public void cancelRefundRequest() {
        if (this.status != PaymentStatus.REFUND_REQUESTED) {
            throw new CustomException(ErrorCode.PAYMENT_CANNOT_CANCEL_REFUND_REQUEST);
        }
        this.status = PaymentStatus.PAID;
    }

    /**
     * 환불 금액 검증 (상태 변경 없음)
     * @param refundAmount 환불할 금액 (전체 결제 금액이어야 함)
     */
    public void validateRefund(int refundAmount) {
        if (this.status != PaymentStatus.REFUND_REQUESTED) {
            throw new CustomException(ErrorCode.PAYMENT_CANNOT_PROCESS_REFUND);
        }
        
        if (refundAmount != this.amount) {
            throw new CustomException(ErrorCode.PAYMENT_INVALID_REFUND_AMOUNT);
        }
    }

    /**
     * 환불 처리 (상태 변경)
     * validateRefund()로 검증 완료 후 호출해야 함
     * @param refundAmount 환불할 금액 (전체 결제 금액)
     */
    public void applyRefund(int refundAmount) {
        this.refundedAmount = refundAmount;
        this.refundedAt = LocalDateTime.now();
        this.status = PaymentStatus.REFUNDED;
    }
}