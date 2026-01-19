package com.example.lionproject2backend.settlement.dto;

import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettlementCalculation {

    private final int totalAmount;
    private final int platformFee;
    private final int settlementAmount;

    public static SettlementCalculation from(List<SettlementDetail> details) {
        int total = 0;
        int fee = 0;
        int settlement = 0;

        for (SettlementDetail d : details) {
            total += d.getPaymentAmount();
            fee += d.getPlatformFee();
            settlement += d.getSettlementAmount();
        }

        return new SettlementCalculation(total, fee, settlement);
    }
}

