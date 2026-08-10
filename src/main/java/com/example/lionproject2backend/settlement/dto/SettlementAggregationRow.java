package com.example.lionproject2backend.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 정산 배치에서 N+1 쿼리를 방지하기 위해, 
 * 멘토별로 1개월 치의 원장 합산 결과를 DB 레벨에서 바로 뽑아오는 DTO입니다.
 */
@Getter
@AllArgsConstructor
public class SettlementAggregationRow {

    private final Long mentorId;
    
    /** PAYMENT 원장 결제금액 합 (양수) */
    private final Long totalPaymentAmount;
    
    /** REFUND 원장 페이백 합 (절대값) */
    private final Long refundAmount;
    
    /** PAYMENT + REFUND 반영 후 수수료 순합산 */
    private final Long platformFee;
    
    /** 전체 순합산 (음수 가능) */
    private final Long settlementAmount;
    
}
