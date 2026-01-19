package com.example.lionproject2backend.settlement.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.payment.domain.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;


    @Column(name = "payment_amount", nullable = false)
    private int paymentAmount;


    @Column(name = "platform_fee", nullable = false)
    private int platformFee;


    @Column(name = "settlement_amount", nullable = false)
    private int settlementAmount;


    public static SettlementDetail create(
                                          Payment payment,
                                          int platformFee
    ) {
        SettlementDetail detail = new SettlementDetail();
        detail.payment = payment;
        detail.paymentAmount = payment.getAmount();
        detail.platformFee = platformFee;
        detail.settlementAmount = detail.paymentAmount - detail.platformFee;
        return detail;
    }

    public void assignSettlement(Settlement settlement) {
        if (this.settlement != null) {
            throw new CustomException(ErrorCode.SETTLEMENT_ALREADY_EXISTS);// 수정하기
        }
        this.settlement = settlement;
    }
}
