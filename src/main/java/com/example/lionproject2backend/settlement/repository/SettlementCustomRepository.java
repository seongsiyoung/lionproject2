package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;

import java.time.YearMonth;
import java.util.List;

public interface SettlementCustomRepository {
    /**
     * 정산 목록 조회 (기간 범위, 상태 필터, 멘토 필터 지원)
     * @param mentorId 멘토 ID (null이면 모든 멘토)
     * @param status 상태 (null이면 모든 상태)
     * @param startPeriod 시작 기간 (null이면 처음부터)
     * @param endPeriod 종료 기간 (null이면 최근까지)
     * @return 정산 목록
     */
    List<Settlement> findSettlements(
            Long mentorId,
            SettlementStatus status,
            YearMonth startPeriod,
            YearMonth endPeriod
    );
}
