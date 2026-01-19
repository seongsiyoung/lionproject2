package com.example.lionproject2backend.payment.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import com.example.lionproject2backend.payment.dto.*;
import com.example.lionproject2backend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PayController {

    private final PaymentService paymentService;

    @PostMapping("/tutorials/{tutorialId}/payments")
    public ResponseEntity<ApiResponse<PaymentCreateResponse>> createPayment(
            @PathVariable Long tutorialId,
            @Valid @RequestBody PaymentCreateRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("결제 생성 요청 - tutorialId: {}, userId: {}, count: {}", tutorialId, userId, request.getCount());
        PaymentCreateResponse response = paymentService.createPayment(tutorialId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("결제가 생성되었습니다.", response));
    }

    /**
     * 결제 검증 및 완료 처리
     * POST /api/payments/{paymentId}/verify
     */
    @PostMapping("/payments/{paymentId}/verify")
    public ResponseEntity<ApiResponse<PaymentVerifyResponse>> verifyPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentVerifyRequest request,
            @AuthenticationPrincipal Long userId 
            //보완(인증 정보 추가)을 위한 userId
    ) {
        log.info("결제 검증 요청 - paymentId: {}, impUid: {}", paymentId, request.getImpUid());
        PaymentVerifyResponse response = paymentService.verifyAndCompletePayment(paymentId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("결제가 완료되었습니다.", response));
    }

    /**
     * 결제 상세 조회
     * GET /api/payments/{paymentId}
     */
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<GetPaymentDetailResponse>> getPaymentDetail(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal Long userId
    ) {
        GetPaymentDetailResponse response = paymentService.getPaymentDetail(paymentId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 결제 목록 조회
     * GET /api/payments?page=0&size=10&status=PAID&keyword=검색어
     */
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<Page<GetPaymentListResponse>>> getPaymentList(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 0, size = 4, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal Long userId
    ) {
        Page<GetPaymentListResponse> response = paymentService.getPaymentList(userId, status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 결제 통계 조회
     * GET /api/payments/stats
     */
    @GetMapping("/payments/stats")
    public ResponseEntity<ApiResponse<GetPaymentStatsResponse>> getPaymentStats(
            @AuthenticationPrincipal Long userId
    ) {
        GetPaymentStatsResponse response = paymentService.getPaymentStats(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 환불 신청 (멘티가 환불 요청)
     * POST /api/payments/{paymentId}/refund/request
     */
    @PostMapping("/payments/{paymentId}/refund/request")
    public ResponseEntity<ApiResponse<Void>> requestRefund(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal Long userId
    ) {
        paymentService.requestRefund(paymentId, userId);
        return ResponseEntity.ok(ApiResponse.success("환불 신청이 완료되었습니다. 관리자 승인을 기다려주세요."));
    }

    /**
     * 환불 승인 (관리자가 환불 승인 및 처리)
     * POST /api/payments/{paymentId}/refund/approve
     */
    @PostMapping("/payments/{paymentId}/refund/approve")
    public ResponseEntity<ApiResponse<Void>> approveRefund(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal Long adminUserId
    ) {
        paymentService.approveRefund(paymentId, adminUserId);
        return ResponseEntity.ok(ApiResponse.success("환불이 승인되었고 처리되었습니다."));
    }

    /**
     * 환불 거절 (관리자가 환불 신청 거절)
     * POST /api/payments/{paymentId}/refund/reject
     */
    @PostMapping("/payments/{paymentId}/refund/reject")
    public ResponseEntity<ApiResponse<Void>> rejectRefund(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal Long adminUserId
    ) {
        paymentService.rejectRefund(paymentId, adminUserId);
        return ResponseEntity.ok(ApiResponse.success("환불 신청이 거절되었습니다."));
    }

    /**
     * 환불 신청 취소 (멘티가 환불 신청 취소)
     * POST /api/payments/{paymentId}/refund/cancel
     */
    @PostMapping("/payments/{paymentId}/refund/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelRefundRequest(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal Long userId
    ) {
        paymentService.cancelRefundRequest(paymentId, userId);
        return ResponseEntity.ok(ApiResponse.success("환불 신청이 취소되었습니다."));
    }

    /**
     * 환불 신청 목록 조회 (관리자용)
     * GET /api/admin/payments/refund-requests
     */
    @GetMapping("/admin/payments/refund-requests")
    public ResponseEntity<ApiResponse<List<GetPaymentListResponse>>> getRefundRequestList(
            @AuthenticationPrincipal Long adminUserId
    ) {
        List<GetPaymentListResponse> response = paymentService.getRefundRequestList(adminUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
