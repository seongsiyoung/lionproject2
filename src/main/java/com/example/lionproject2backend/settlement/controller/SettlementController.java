package com.example.lionproject2backend.settlement.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import com.example.lionproject2backend.settlement.dto.*;
import com.example.lionproject2backend.settlement.service.SettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;
    private final JobLauncher jobLauncher;
    private final Job settlementJob;

    /**
     * 정산 생성 (Spring Batch Job 실행)
     * POST /api/settlements
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSettlements(
            @Valid @RequestBody PostSettlementCreateRequest request
    ) {
        try {
            YearMonth settlementPeriod = request.toYearMonth();
            String settlementPeriodStr = settlementPeriod.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // JobParameters 생성 (중복 실행 방지를 위해 timestamp 추가)
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("settlementPeriod", settlementPeriodStr)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Job 실행
            jobLauncher.run(settlementJob, jobParameters);

            String successMessage = "정산 생성 Job이 시작되었습니다. settlementPeriod: " + settlementPeriodStr;
            return ResponseEntity.ok(ApiResponse.success(successMessage));
        } catch (Exception e) {
            String errorMessage = "정산 생성 Job 실행 중 오류 발생: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("JOB_EXECUTION_ERROR", errorMessage));
        }
    }

    /**
     * 정산 지급 완료 처리
     * POST /api/settlements/{settlementId}/complete
     */
    @PostMapping("/{settlementId}/complete")
    public ResponseEntity<ApiResponse<SettlementResponse>> completeSettlement(
            @PathVariable Long settlementId
    ) {
        SettlementResponse response = settlementService.completeSettlement(settlementId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 정산 상세 조회
     * GET /api/settlements/{settlementId}
     */
    @GetMapping("/{settlementId}")
    public ResponseEntity<ApiResponse<GetSettlementDetailResponse>> getSettlementDetail(
            @PathVariable Long settlementId,
            @AuthenticationPrincipal Long userId
    ) {
        GetSettlementDetailResponse response = settlementService.getSettlementDetail(settlementId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 정산 목록 조회
     * GET /api/settlements?status=PENDING&startPeriod=2024-01&endPeriod=2024-12
     * 
     * @param status 정산 상태 (선택)
     * @param startPeriod 시작 기간 "YYYY-MM" 형식 (선택, null이면 처음부터)
     * @param endPeriod 종료 기간 "YYYY-MM" 형식 (선택, null이면 최근까지)
     *
     * 멘토: 자신의 정산만 조회
     * 관리자: 모든 정산 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getSettlementList(
            @RequestParam(required = false) SettlementStatus status,
            @RequestParam(required = false) String startPeriod,
            @RequestParam(required = false) String endPeriod,
            @AuthenticationPrincipal Long userId
    ) {
        YearMonth start = getYearMonth(startPeriod);
        YearMonth end = getYearMonth(endPeriod);

        List<SettlementResponse> response = settlementService.getSettlementList(userId, status, start, end);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private static YearMonth getYearMonth(String period) {
        return period != null
                ? YearMonth.parse(period, DateTimeFormatter.ofPattern("yyyy-MM"))
                : null;
    }
}
