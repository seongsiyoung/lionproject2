package com.example.lionproject2backend.settlement.dto;

import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementDetailType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 원장 집계 결과 DTO
 * - totalPaymentAmount: PAYMENT 원장 결제금액 합 (양수)
 * - refundAmount: REFUND 원장 절대값 합 (표시용)
 * - platformFee: PAYMENT + REFUND 반영 후 수수료 순합산
 * - settlementAmount: 전체 순합산 (음수 가능)
 */
@Getter
@AllArgsConstructor
public class SettlementCalculation {

    private final int totalPaymentAmount;
    private final int refundAmount;
    private final int platformFee;
    private final int settlementAmount;

    public static SettlementCalculation from(List<SettlementDetail> details) {
        int totalPayment = 0;
        int refund = 0;
        int fee = 0;
        int settlement = 0;

        for (SettlementDetail d : details) {
            if (d.getType() == SettlementDetailType.PAYMENT) {
                totalPayment += d.getPaymentAmount();
            } else if (d.getType() == SettlementDetailType.REFUND) {
                refund += Math.abs(d.getPaymentAmount());
            }
            fee += d.getPlatformFee();
            settlement += d.getSettlementAmount();
        }

        return new SettlementCalculation(totalPayment, refund, fee, settlement);
    }
}
