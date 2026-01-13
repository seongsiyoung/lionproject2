package com.example.lionproject2backend.payment.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.payment.dto.PaymentCreateRequest;
import com.example.lionproject2backend.payment.dto.PaymentCreateResponse;
import com.example.lionproject2backend.payment.dto.PaymentVerifyRequest;
import com.example.lionproject2backend.payment.dto.PaymentVerifyResponse;
import com.example.lionproject2backend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PayController {

    private final PaymentService paymentService;

    /**
     * ========== 변경 전 코드 (기존 Controller) ==========
     *
     * @PostMapping("/payments/verify")
     * public ResponseEntity<?> verifyPayment(@RequestBody VerifyPaymentRequest request) {
     *     try {
     *         String paymentId = request.paymentId();
     *         int amount = request.amount();
     *
     *         log.info("Payment verify request - paymentId: {}, amount: {}", paymentId, amount);
     *
     *         paymentService.verifyCompletedPayment(paymentId, amount);
     *
     *         return ResponseEntity.ok("Payment verification completed");
     *     } catch (Exception e) {
     *         log.error("Payment verification failed: {}", e.getMessage());
     *         return ResponseEntity.internalServerError().body(e.getMessage());
     *     }
     * }
     *
     * public record VerifyPaymentRequest(String paymentId, int amount) {}
     *

     * ========== 변경 전 코드 끝 ==========
     */

    /**
     * 결제 생성 (PENDING 상태)
     * POST /api/tutorials/{tutorialId}/payments
     */
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
            @Valid @RequestBody PaymentVerifyRequest request
    ) {
        log.info("결제 검증 요청 - paymentId: {}, impUid: {}", paymentId, request.getImpUid());
        PaymentVerifyResponse response = paymentService.verifyAndCompletePayment(paymentId, request);
        return ResponseEntity.ok(ApiResponse.success("결제가 완료되었습니다.", response));
    }
}
