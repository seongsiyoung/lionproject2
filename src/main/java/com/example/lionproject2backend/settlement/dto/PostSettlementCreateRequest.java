package com.example.lionproject2backend.settlement.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 정산 생성 요청
 * settlementPeriod 형식: "YYYY-MM" (예: "2024-11")
 */
@Getter
public class PostSettlementCreateRequest {

    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "정산 기간은 YYYY-MM 형식이어야 합니다.")
    private String settlementPeriod;

    /**
     * YearMonth로 변환
     */
    public YearMonth toYearMonth() {
        if (settlementPeriod == null) {
            return null;
        }
        return YearMonth.parse(settlementPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
