package com.example.lionproject2backend.settlement.dto;

import com.example.lionproject2backend.settlement.domain.Settlement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetSettlementDetailResponse {

    private SettlementResponse settlement;
    private List<SettlementDetailResponse> details;

    public static GetSettlementDetailResponse of(Settlement settlement, List<SettlementDetailResponse> details) {
        return GetSettlementDetailResponse.builder()
                .settlement(SettlementResponse.from(settlement))
                .details(details)
                .build();
    }
}
