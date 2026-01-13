package com.example.lionproject2backend.ticket.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id", nullable = false)
    private Tutorial tutorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "remaining_count", nullable = false)
    private int remainingCount;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    // 생성 메서드
    public static Ticket create(Payment payment, Tutorial tutorial, User mentee, int count) {
        Ticket ticket = new Ticket();
        ticket.payment = payment;
        ticket.tutorial = tutorial;
        ticket.mentee = mentee;
        ticket.totalCount = count;
        ticket.remainingCount = count;
        ticket.expiredAt = LocalDateTime.now().plusMonths(6);
        return ticket;
    }

    // 이용권 사용
    public void use() {
        if (this.remainingCount <= 0) {
            throw new IllegalStateException("남은 이용권이 없습니다");
        }
        if (isExpired()) {
            throw new IllegalStateException("이용권이 만료되었습니다");
        }
        this.remainingCount--;
    }

    // 이용권 복구 (거절 시)
    public void restore() {
        if (this.remainingCount >= this.totalCount) {
            throw new IllegalStateException("복구할 수 없습니다");
        }
        this.remainingCount++;
    }

    public boolean isExpired() {
        return expiredAt != null && LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean hasRemaining() {
        return this.remainingCount > 0 && !isExpired();
    }
}