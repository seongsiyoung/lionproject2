package com.example.lionproject2backend.payment.service;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import com.example.lionproject2backend.payment.dto.*;
import com.example.lionproject2backend.payment.infra.PortOneClient;
import com.example.lionproject2backend.payment.repository.PaymentRepository;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.service.SettlementService;
import com.example.lionproject2backend.ticket.domain.Ticket;
import com.example.lionproject2backend.ticket.repository.TicketRepository;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.repository.TutorialRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import com.example.lionproject2backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    private final SettlementService settlementService;
    private final SettlementDetailRepository settlementDetailRepository;
    private static final int PLATFORM_FEE_PERCENT = 10; // 10%


    @Transactional
    public PaymentCreateResponse createPayment(Long tutorialId, Long userId, PaymentCreateRequest request) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new CustomException(ErrorCode.TUTORIAL_NOT_FOUND));

        User mentee = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        //본인 확인하는 로직 추가
        if (!payment.getMentee().getId().equals(userId)) {
            log.error("보안 경고: 타인의 결제 검증 시도 - 유저ID: {}, 결제ID: {}", userId, paymentId);
            throw new CustomException(ErrorCode.PAYMENT_FORBIDDEN);
        }
        //이미 완료된 건은 포트원에서 호출하지x 종료
        if (payment.getStatus() == PaymentStatus.PAID) {
            log.info("이미 완료된 결제 건입니다. 기존 이용권 정보를 반환합니다. - paymentId: {}", paymentId);
            Ticket ticket = ticketRepository.findByPayment(payment)
                    .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));
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
            throw new CustomException(ErrorCode.PAYMENT_NOT_COMPLETED);
        }

        // 4. 금액 검증
        if (actualAmount != payment.getAmount()) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 5. Payment 완료 처리
        String merchantUid = getString(paymentData, "id");
        payment.complete(request.getImpUid(), merchantUid);

        // 6. Ticket 생성
        Ticket ticket = Ticket.create(payment, payment.getTutorial(), payment.getMentee(), payment.getCount());
        Ticket savedTicket = ticketRepository.save(ticket);

        int platformFee = payment.getAmount() * PLATFORM_FEE_PERCENT / 100;
        SettlementDetail settlementDetail = SettlementDetail.create(payment, platformFee );
        settlementDetailRepository.save(settlementDetail);

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

    /**
     * 결제 상세 조회 (모든 상태)
     */
    @Transactional(readOnly = true)
    public GetPaymentDetailResponse getPaymentDetail(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findByIdAndMenteeId(paymentId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        return GetPaymentDetailResponse.from(payment);
    }

    /**
     * 결제 목록 조회 (페이지네이션, 필터링, 검색)
     */
    @Transactional(readOnly = true)
    public Page<GetPaymentListResponse> getPaymentList(
            Long userId,
            PaymentStatus status,
            String keyword,
            Pageable pageable
    ) {
        Page<Payment> payments = paymentRepository.findByMenteeIdWithFilters(userId, status, keyword, pageable);
        return payments.map(GetPaymentListResponse::from);
    }

    /**
     * 결제 통계 조회
     */
    @Transactional(readOnly = true)
    public GetPaymentStatsResponse getPaymentStats(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        int totalAmount = paymentRepository.sumPaidAmountByMentee(userId);

        int monthlyAmount =
                paymentRepository.sumPaidAmountByMenteeAndPaidAtBetween(
                        userId, startOfMonth, endOfMonth
                );

        long activeTutorialCount = ticketRepository.countActiveTutorials(userId);

        return GetPaymentStatsResponse.of(
                totalAmount,
                monthlyAmount,
                activeTutorialCount
        );
    }

    /**
     * 환불 신청 (멘티가 환불 요청)
     * 상태만 REFUND_REQUESTED로 변경, 실제 환불은 관리자 승인 후 처리
     * 전체 환불만 가능: 한 번이라도 레슨을 받으면 환불 불가
     */
    @Transactional
    public void requestRefund(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        validateUser(userId, payment);

        Ticket ticket = ticketRepository.findByPayment(payment)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        ticket.validateRefund();
        payment.requestRefund();

        log.info("환불 신청 완료 - paymentId: {}, userId: {}", paymentId, userId);
    }

    /**
     * 환불 승인 및 처리 (관리자가 환불 승인)
     * PortOne API를 통한 실제 환불 요청 후 시스템 상태 변경
     * 전체 환불만 가능: 한 번이라도 레슨을 받으면 환불 불가
     */
    @Transactional
    public void approveRefund(Long paymentId, Long adminUserId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        validateAdminAuthority(adminUserId);

        Ticket ticket = ticketRepository.findByPayment(payment)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        ticket.validateRefund();

        int refundAmount = ticket.calculateRefundAmount();

        payment.validateRefund(refundAmount);

        // PortOne 환불 API 호출 (실제 환불 처리)
        /**
         * 환불 구현 필요
         * 환불 실패 시 커스텀 에러 발생하도록 !
         */

        payment.applyRefund(refundAmount);
        /**
         * 티켓 상태 변경?
         */
        ticket.applyRefund();

        settlementService.recordRefundAdjustment(payment);

        log.info("환불 승인 및 처리 완료 - paymentId: {}, adminUserId: {}, refundAmount: {}",
                paymentId, adminUserId, refundAmount);
    }

    /**
     * 환불 거절 (관리자가 환불 신청 거절)
     * Payment 상태를 원래대로 복구
     */
    @Transactional
    public void rejectRefund(Long paymentId, Long adminUserId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        validateAdminAuthority(adminUserId);

        payment.rejectRefund();

        log.info("환불 거절 완료 - paymentId: {}, adminUserId: {}", paymentId, adminUserId);
    }

    private void validateAdminAuthority(Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (admin.getUserRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.PAYMENT_ADMIN_REQUIRED);
        }
    }

    /**
     * 환불 신청 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public List<GetPaymentListResponse> getRefundRequestList(Long adminUserId) {

        validateAdminAuthority(adminUserId);

        List<Payment> refundRequests = paymentRepository.findAllRefundRequests();

        return refundRequests.stream()
                .map(GetPaymentListResponse::from)
                .toList();
    }

    /**
     * 환불 신청 취소
     */
    @Transactional
    public void cancelRefundRequest(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        validateUser(userId, payment);

        payment.cancelRefundRequest();

        log.info("환불 신청 취소 완료 - paymentId: {}, userId: {}", paymentId, userId);
    }

    private void validateUser(Long userId, Payment payment) {
        if (!payment.getMentee().getId().equals(userId)) {
            throw new CustomException(ErrorCode.PAYMENT_FORBIDDEN);
        }
    }
}
