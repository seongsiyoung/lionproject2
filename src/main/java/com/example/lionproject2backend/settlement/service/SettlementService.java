package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementDetailType;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import com.example.lionproject2backend.settlement.dto.*;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import com.example.lionproject2backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    private static final int PLATFORM_FEE_PERCENT = 10;

    /**
     * 정산 생성 (불변 이벤트 원장 기반)
     * 1. 이번 달 미정산 원장(PAYMENT + REFUND) 조회
     * 2. 멘토별 집계 (SUM)
     * 3. 이전 누적 미소진 이월액 반영
     * 4. payableAmount / carryOverAmount 계산
     */
    @Transactional
    public List<SettlementResponse> createSettlements(YearMonth settlementPeriod) {

        List<SettlementDetail> details =
                settlementDetailRepository.findUnsettledDetailsByPeriod(settlementPeriod);

        if (details.isEmpty()) {
            log.info("정산할 내역 없음. period={}", settlementPeriod);
            return List.of();
        }

        Map<Mentor, List<SettlementDetail>> detailsByMentor =
                details.stream().collect(Collectors.groupingBy(
                        d -> d.getPayment().getTutorial().getMentor()
                ));

        List<Settlement> settlements = new ArrayList<>();

        detailsByMentor.forEach((mentor, mentorDetails) -> {

            // 1. 원장 집계 (PAYMENT 양수 + REFUND 음수 → 순합산)
            SettlementCalculation calc = SettlementCalculation.from(mentorDetails);

            // 2. 이전 누적 미소진 이월액 조회
            int previousCarryOver = getOutstandingCarryOver(mentor, settlementPeriod);

            // 3. 지급액/이월액 계산
            int adjustedNet = calc.getSettlementAmount() - previousCarryOver;
            int payableAmount = Math.max(0, adjustedNet);
            int carryOverAmount = Math.max(0, -adjustedNet);

            // 4. Settlement 생성
            Settlement settlement = Settlement.create(
                    mentor,
                    settlementPeriod,
                    calc.getTotalPaymentAmount(),
                    calc.getPlatformFee(),
                    calc.getSettlementAmount(),
                    calc.getRefundAmount(),
                    previousCarryOver,
                    payableAmount,
                    carryOverAmount
            );

            settlementRepository.save(settlement);

            // 5. 원장에 settlement_id 연결
            mentorDetails.forEach(d -> d.assignSettlement(settlement));
            settlementDetailRepository.saveAll(mentorDetails);

            settlements.add(settlement);
        });

        log.info("정산 생성 완료 - period={}, count={}", settlementPeriod, settlements.size());

        return settlements.stream()
                .map(SettlementResponse::from)
                .toList();
    }

    /**
     * 정산 지급 완료 처리
     */
    @Transactional
    public SettlementResponse completeSettlement(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));

        if (settlement.getStatus() == SettlementStatus.COMPLETED) {
            throw new CustomException(ErrorCode.SETTLEMENT_ALREADY_COMPLETED);
        }

        settlement.complete();

        log.info("정산 지급 완료 처리. settlementId: {}, mentorId: {}, payableAmount: {}",
                settlementId, settlement.getMentor().getId(), settlement.getPayableAmount());

        return SettlementResponse.from(settlement);
    }

    /**
     * 정산 상세 조회
     */
    @Transactional(readOnly = true)
    public GetSettlementDetailResponse getSettlementDetail(Long settlementId, Long userId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserRole() != UserRole.ADMIN) {
            Mentor mentor = mentorRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_ACCESS_DENIED));

            if (!settlement.getMentor().getId().equals(mentor.getId())) {
                throw new CustomException(ErrorCode.SETTLEMENT_ACCESS_DENIED);
            }
        }

        List<SettlementDetailResponse> details = settlementDetailRepository
                .findBySettlementIdOrderByCreatedAtDesc(settlementId)
                .stream()
                .map(SettlementDetailResponse::from)
                .toList();

        return GetSettlementDetailResponse.of(settlement, details);
    }

    /**
     * 정산 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlementList(
            Long userId,
            SettlementStatus status,
            YearMonth startPeriod,
            YearMonth endPeriod
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long mentorId = null;
        if (user.getUserRole() != UserRole.ADMIN) {
            Mentor mentor = mentorRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MENTOR_FORBIDDEN));
            mentorId = mentor.getId();
        }

        return settlementRepository.findSettlements(
                        mentorId,
                        status,
                        startPeriod,
                        endPeriod
                ).stream()
                .map(SettlementResponse::from)
                .toList();
    }

    /**
     * 환불 시 마이너스 원장 생성 (불변 이벤트 원장)
     * PaymentService에서 호출
     * @param refundedPayment 환불된 결제
     */
    @Transactional
    public void createRefundSettlementDetail(Payment refundedPayment) {

        // 서비스단 중복 체크 (DB 유니크 제약은 마지막 안전망)
        if (settlementDetailRepository.existsByPaymentAndType(
                refundedPayment, SettlementDetailType.REFUND)) {
            log.warn("이미 환불 원장이 존재합니다. paymentId={}", refundedPayment.getId());
            return;
        }

        int platformFee = refundedPayment.getAmount() * PLATFORM_FEE_PERCENT / 100;
        SettlementDetail refundDetail = SettlementDetail.createRefund(
                refundedPayment, platformFee, LocalDateTime.now()
        );
        settlementDetailRepository.save(refundDetail);

        log.info("환불 원장 생성 - paymentId={}, amount=-{}",
                refundedPayment.getId(), refundedPayment.getAmount());
    }

    /**
     * 누적 미소진 이월액 조회
     * 직전 Settlement의 carryOverAmount를 가져온다.
     * (이월은 누적되어 다음 달 Settlement에 반영되므로 직전 것만 보면 됨)
     */
    private int getOutstandingCarryOver(Mentor mentor, YearMonth currentPeriod) {
        YearMonth previousPeriod = currentPeriod.minusMonths(1);
        return settlementRepository
                .findByMentorAndSettlementPeriod(mentor, previousPeriod)
                .map(Settlement::getCarryOverAmount)
                .orElse(0);
    }

    @Transactional
    public int completeSettlementsByPeriod(YearMonth period) {
        List<Settlement> settlements =
                settlementRepository.findBySettlementPeriodAndStatus(period, SettlementStatus.PENDING);

        for (Settlement settlement : settlements) {
            settlement.complete();
        }

        return settlements.size();
    }
}
