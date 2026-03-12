package com.example.lionproject2backend.settlement.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.payment.domain.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "settlement_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_settlement_detail_payment_type",
                        columnNames = {"payment_id", "type"}
                )
        }
)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private SettlementDetailType type;

    @Column(name = "payment_amount", nullable = false)
    private int paymentAmount;

    @Column(name = "platform_fee", nullable = false)
    private int platformFee;

    @Column(name = "settlement_amount", nullable = false)
    private int settlementAmount;

    /**
     * 원장 발생 시각
     * PAYMENT: 결제 확정 시각
     * REFUND: 환불 확정 시각
     */
    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    /**
     * 결제 원장 생성 (+)
     * paymentAmount, platformFee, settlementAmount 모두 양수
     */
    public static SettlementDetail createPayment(Payment payment, int platformFee, LocalDateTime occurredAt) {
        SettlementDetail detail = new SettlementDetail();
        detail.payment = payment;
        detail.type = SettlementDetailType.PAYMENT;
        detail.paymentAmount = payment.getAmount();
        detail.platformFee = platformFee;
        detail.settlementAmount = detail.paymentAmount - detail.platformFee;
        detail.occurredAt = occurredAt;
        return detail;
    }

    /**
     * 환불 원장 생성 (-)
     * paymentAmount, platformFee, settlementAmount 모두 음수로 기록
     */
    public static SettlementDetail createRefund(Payment payment, int platformFee, LocalDateTime occurredAt) {
        SettlementDetail detail = new SettlementDetail();
        detail.payment = payment;
        detail.type = SettlementDetailType.REFUND;
        detail.paymentAmount = -payment.getAmount();
        detail.platformFee = -platformFee;
        detail.settlementAmount = detail.paymentAmount - detail.platformFee;
        detail.occurredAt = occurredAt;
        return detail;
    }

    /**
     * 배치에서 정산 완료 시 settlement_id 연결
     */
    public void assignSettlement(Settlement settlement) {
        if (this.settlement != null) {
            throw new CustomException(ErrorCode.SETTLEMENT_ALREADY_EXISTS);
        }
        this.settlement = settlement;
    }
}
