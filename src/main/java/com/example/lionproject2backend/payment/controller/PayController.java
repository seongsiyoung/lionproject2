package com.example.lionproject2backend.payment.controller;

import com.example.lionproject2backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PayController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody VerifyPaymentRequest request) {
        try {
            String paymentId = request.paymentId();
            int amount = request.amount();

            log.info("Payment verify request - paymentId: {}, amount: {}", paymentId, amount);

            paymentService.verifyCompletedPayment(paymentId, amount);

            return ResponseEntity.ok("Payment verification completed");
        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public record VerifyPaymentRequest(String paymentId, int amount) {
    }
}
