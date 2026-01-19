package com.example.lionproject2backend.settlement.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.settlement.util.YearMonthAttributeConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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


    @Column(name = "total_amount", nullable = false)
    private int totalAmount;


    @Column(name = "platform_fee", nullable = false)
    private int platformFee;

    @Column(name = "settlement_amount", nullable = false)
    private int settlementAmount;

    @Column(name = "final_settlement_amount", nullable = false)
    private int finalSettlementAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SettlementStatus status;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;


    /**
     * 정산 생성
     * @param mentor 멘토
     * @param settlementPeriod 정산 기간
     * @param totalAmount 총 결제 금액
     */
    public static Settlement create(Mentor mentor,
                                    YearMonth settlementPeriod,
                                    int totalAmount,
                                    int platformFee,
                                    int settlementAmount) {
        Settlement settlement = new Settlement();
        settlement.mentor = mentor;
        settlement.settlementPeriod = settlementPeriod;
        settlement.totalAmount = totalAmount;
        settlement.platformFee = platformFee;
        settlement.settlementAmount = settlementAmount;
        settlement.finalSettlementAmount = settlement.settlementAmount;
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

    public void applyFinalAmount(int finalAmount) {
        this.finalSettlementAmount = finalAmount;
    }
}
