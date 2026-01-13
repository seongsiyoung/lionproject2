package com.example.lionproject2backend.payment.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
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
@AllArgsConstructor
@Builder
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
            throw new IllegalStateException("대기중인 결제만 완료할 수 있습니다");
        }
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    // 결제 취소
    public void cancel() {
        if (this.status == PaymentStatus.PAID) {
            throw new IllegalStateException("완료된 결제는 취소할 수 없습니다");
        }
        this.status = PaymentStatus.CANCELLED;
    }
}