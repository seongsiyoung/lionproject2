package com.example.lionproject2backend.payment.service;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import com.example.lionproject2backend.payment.infra.PortOneClient;
import com.example.lionproject2backend.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PortOneClient portOneClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void verifyCompletedPayment(String paymentId, int expectedAmount) {
        Map<String, Object> response = portOneClient.getPaymentDetails(paymentId);
        log.info("PortOne Response Raw Data: {}", response);

        // 1. 응답 데이터 결정 (payment 키가 있으면 그 안의 데이터를, 없으면 응답 자체를 사용)
        Map<String, Object> paymentData;
        if (response.containsKey("payment")) {
            paymentData = (Map<String, Object>) response.get("payment");
        } else {
            paymentData = response;
        }

        // 2. 데이터 추출 (로그 구조에 맞게 수정)
        String status = getString(paymentData, "status");
        Map<String, Object> amountData = getMap(paymentData, "amount");
        int actualAmount = getNumber(amountData, "total");

        log.info("검증 시도 - 상태: {}, 금액: {}", status, actualAmount);

        // 3. 검증 로직
        if (!"PAID".equals(status)) {
            throw new IllegalStateException("결제가 완료되지 않았습니다. 현재 상태: " + status);
        }

        if (actualAmount != expectedAmount) {
            throw new IllegalArgumentException("금액 불일치! 기대금액: " + expectedAmount + ", 실제: " + actualAmount);
        }

        // 4. DB 저장 (V2는 'id' 필드가 주문번호입니다)
        Payment payment = Payment.builder()
            .impUid(paymentId)
            .merchantUid(getString(paymentData, "id"))
            .amount(actualAmount)
            .paymentStatus(PaymentStatus.PAID)
            .build();

        paymentRepository.save(payment);
        log.info("✅ 결제 검증 및 DB 저장 성공! - ID: {}", paymentId);
    }

    // 헬퍼 메소드들
    private Map<String, Object> getMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        return (Map<String, Object>) value;
    }

    private int getNumber(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
    }

    private String getString(Map<String, Object> source, String key) {
        Object value = source.get(key);
        return value != null ? value.toString() : null;
    }
}