package com.example.lionproject2backend.payment.service;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus; 
import com.example.lionproject2backend.payment.dto.PaymentCreateRequest;
import com.example.lionproject2backend.payment.dto.PaymentCreateResponse;
import com.example.lionproject2backend.payment.dto.PaymentVerifyRequest;
import com.example.lionproject2backend.payment.dto.PaymentVerifyResponse;
import com.example.lionproject2backend.payment.infra.PortOneClient;
import com.example.lionproject2backend.payment.repository.PaymentRepository;
import com.example.lionproject2backend.ticket.domain.Ticket;
import com.example.lionproject2backend.ticket.repository.TicketRepository;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.repository.TutorialRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException; 
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final PortOneClient portOneClient;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final TutorialRepository tutorialRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentCreateResponse createPayment(Long tutorialId, Long userId, PaymentCreateRequest request) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과외입니다."));

        User mentee = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Payment payment = Payment.create(tutorial, mentee, request.getCount());
        Payment savedPayment = paymentRepository.save(payment);

        log.info("결제 생성 완료 - paymentId: {}, amount: {}", savedPayment.getId(), savedPayment.getAmount());

        return PaymentCreateResponse.from(savedPayment);
    }

    /**
     * 결제 검증 및 완료 처리
     * POST /api/payments/{paymentId}/verify
     */
    @Transactional
    public PaymentVerifyResponse verifyAndCompletePayment(Long paymentId, PaymentVerifyRequest request,Long userId) {
        // 1. Payment 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        //본인 확인하는 로직 추가
        if (!payment.getMentee().getId().equals(userId)) {
            log.error("보안 경고: 타인의 결제 검증 시도 - 유저ID: {}, 결제ID: {}", userId, paymentId);
            throw new AccessDeniedException("본인의 결제 건만 검증할 수 있습니다.");
        }
        //이미 완료된 건은 포트원에서 호출하지x 종료
        if (payment.getStatus() == PaymentStatus.PAID) {
            log.info("이미 완료된 결제 건입니다. 기존 이용권 정보를 반환합니다. - paymentId: {}", paymentId);
            Ticket ticket = ticketRepository.findByPayment(payment)
                    .orElseThrow(() -> new IllegalStateException("결제는 완료되었으나 이용권이 존재하지 않습니다."));
            return PaymentVerifyResponse.from(payment, ticket);
        }
        // 2. PortOne 결제 검증
        Map<String, Object> response = portOneClient.getPaymentDetails(request.getImpUid());
        log.info("PortOne Response Raw Data: {}", response);

        Map<String, Object> paymentData;
        if (response.containsKey("payment")) {
            paymentData = (Map<String, Object>) response.get("payment");
        } else {
            paymentData = response;
        }

        String status = getString(paymentData, "status");
        Map<String, Object> amountData = getMap(paymentData, "amount");
        int actualAmount = getNumber(amountData, "total");

        log.info("검증 시도 - 상태: {}, 금액: {}", status, actualAmount);

        // 3. 상태 검증
        if (!"PAID".equals(status)) {
            throw new IllegalStateException("결제가 완료되지 않았습니다. 현재 상태: " + status);
        }

        // 4. 금액 검증
        if (actualAmount != payment.getAmount()) {
            throw new IllegalArgumentException("금액 불일치! 기대금액: " + payment.getAmount() + ", 실제: " + actualAmount);
        }

        // 5. Payment 완료 처리
        String merchantUid = getString(paymentData, "id");
        payment.complete(request.getImpUid(), merchantUid);

        // 6. Ticket 생성
        Ticket ticket = Ticket.create(payment, payment.getTutorial(), payment.getMentee(), payment.getCount());
        Ticket savedTicket = ticketRepository.save(ticket);

        log.info("결제 검증 및 이용권 생성 완료 - paymentId: {}, ticketId: {}", paymentId, savedTicket.getId());

        return PaymentVerifyResponse.from(payment, savedTicket);
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
