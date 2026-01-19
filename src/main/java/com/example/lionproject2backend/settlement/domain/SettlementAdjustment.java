package com.example.lionproject2backend.settlement.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.settlement.util.YearMonthAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.YearMonth;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settlement_adjustments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementAdjustment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    //어느 정산 기간에 반영할지
    @Column(name = "target_period", nullable = false, length = 7)
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth targetPeriod;


    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int appliedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdjustmentType type;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdjustmentStatus status;


    // 이벤트 발생 시각 (환불 승인 시점 등)
    @Column(nullable = false)
    private LocalDateTime occurredAt;

    // 전액 적용 완료 시각
    private LocalDateTime appliedAt;

    public static SettlementAdjustment create(
            Mentor mentor,
            YearMonth targetPeriod,
            int amount,
            AdjustmentType type
    ) {
        if (amount <= 0) {
            throw new IllegalArgumentException("조정 금액은 0보다 커야 합니다.");
        }

        SettlementAdjustment adjustment = new SettlementAdjustment();
        adjustment.mentor = mentor;
        adjustment.targetPeriod = targetPeriod;
        adjustment.amount = amount;
        adjustment.type = type;
        adjustment.status = AdjustmentStatus.PENDING;
        adjustment.occurredAt = LocalDateTime.now();

        return adjustment;
    }

    public int getRemainingAmount() {
        return amount - appliedAmount;
    }

    public void applyPartially(int applyAmount) {
        if (applyAmount <= 0) return;

        this.appliedAmount += applyAmount;

        if (this.appliedAmount < this.amount) {
            this.status = AdjustmentStatus.PARTIALLY_APPLIED;
        } else {
            this.status = AdjustmentStatus.APPLIED;
            this.appliedAt = LocalDateTime.now();
        }
    }
}