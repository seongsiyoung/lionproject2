package com.example.lionproject2backend.settlement.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.settlement.util.YearMonthAttributeConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(
        name = "settlements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_settlement_mentor_period",
                        columnNames = {"mentor_id", "settlement_period"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Column(name = "settlement_period", nullable = false, length = 7)
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth settlementPeriod;

    /** PAYMENT 원장 결제금액 합계 */
    @Column(name = "total_payment_amount", nullable = false)
    private int totalPaymentAmount;

    /** 수수료 순합산  */
    @Column(name = "platform_fee", nullable = false)
    private int platformFee;

    /** 순정산액: PAYMENT + REFUND 모두 반영한 SUM (음수 가능) */
    @Column(name = "settlement_amount", nullable = false)
    private int settlementAmount;

    /** REFUND 원장 절대값 합 */
    @Column(name = "refund_amount", nullable = false)
    private int refundAmount;

    /** 이전 누적 미소진 이월액 */
    @Column(name = "previous_carry_over_amount", nullable = false)
    private int previousCarryOverAmount;

    /** 실제 지급액 = max(0, settlementAmount - previousCarryOverAmount) */
    @Column(name = "payable_amount", nullable = false)
    private int payableAmount;

    /** 다음 달 이월 차감액 = max(0, -(settlementAmount - previousCarryOverAmount)) */
    @Column(name = "carry_over_amount", nullable = false)
    private int carryOverAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SettlementStatus status;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    public static Settlement create(
            Mentor mentor,
            YearMonth settlementPeriod,
            int totalPaymentAmount,
            int platformFee,
            int settlementAmount,
            int refundAmount,
            int previousCarryOverAmount,
            int payableAmount,
            int carryOverAmount
    ) {
        Settlement settlement = new Settlement();
        settlement.mentor = mentor;
        settlement.settlementPeriod = settlementPeriod;
        settlement.totalPaymentAmount = totalPaymentAmount;
        settlement.platformFee = platformFee;
        settlement.settlementAmount = settlementAmount;
        settlement.refundAmount = refundAmount;
        settlement.previousCarryOverAmount = previousCarryOverAmount;
        settlement.payableAmount = payableAmount;
        settlement.carryOverAmount = carryOverAmount;
        settlement.status = SettlementStatus.PENDING;
        return settlement;
    }

    public void complete() {
        if (this.status == SettlementStatus.COMPLETED) {
            throw new IllegalStateException("이미 지급 완료된 정산입니다.");
        }
        this.status = SettlementStatus.COMPLETED;
        this.settledAt = LocalDateTime.now();
    }
}
