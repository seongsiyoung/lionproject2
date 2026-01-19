package com.example.lionproject2backend.settlement.service;

import static com.example.lionproject2backend.mentor.domain.QMentor.mentor;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.repository.PaymentRepository;
import com.example.lionproject2backend.settlement.domain.AdjustmentType;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementAdjustment;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import com.example.lionproject2backend.settlement.dto.*;
import com.example.lionproject2backend.settlement.repository.SettlementAdjustmentRepository;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import com.example.lionproject2backend.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final SettlementAdjustmentRepository settlementAdjustmentRepository;
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    /**
     * 정산 생성 (특정 기간의 결제 내역 조회 후 멘토별로 정산 생성)
     * @return 생성된 정산 목록
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
            SettlementCalculation calc = SettlementCalculation.from(mentorDetails);

            Settlement settlement = Settlement.create(
                    mentor,
                    settlementPeriod,
                    calc.getTotalAmount(),
                    calc.getPlatformFee(),
                    calc.getSettlementAmount()
            );

            settlementRepository.save(settlement);

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

        calculateFinalSettlementAmount(settlementId);
        settlement.complete();

        log.info("정산 지급 완료 처리. settlementId: {}, mentorId: {}, settlementAmount: {}",
                settlementId, settlement.getMentor().getId(), settlement.getFinalSettlementAmount());

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
     * 멘토: 자신의 정산만 조회
     * 관리자: 모든 정산 조회
     * - startPeriod가 null이고 endPeriod가 있으면: 처음부터 endPeriod까지
     * - startPeriod가 있고 endPeriod가 null이면: startPeriod부터 최근까지
     * - 둘 다 null이면: 모든 기간
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
     * 환불 처리 시 정산 금액 조정 (전체 환불만 가능)
     * PaymentService에서 호출
     * @param refundedPayment 환불된 결제
     */
    @Transactional
    public void recordRefundAdjustment(Payment refundedPayment) {

        SettlementDetail detail = settlementDetailRepository
                .findByPayment(refundedPayment)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_DETAIL_NOT_FOUND));

        int mentorRefundAmount = detail.getSettlementAmount();

        YearMonth targetPeriod =
                YearMonth.from(refundedPayment.getPaidAt()).plusMonths(1);

        SettlementAdjustment adjustment = SettlementAdjustment.create(
                refundedPayment.getTutorial().getMentor(),
                targetPeriod,
                mentorRefundAmount,
                AdjustmentType.REFUND
        );

        settlementAdjustmentRepository.save(adjustment);

        log.info("환불 조정 기록 생성 - paymentId={}, period={}, amount={}",
                refundedPayment.getId(),
                targetPeriod,
                mentorRefundAmount
        );
    }



    @Transactional
    public void calculateFinalSettlementAmount(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));

        YearMonth period = settlement.getSettlementPeriod();
        Mentor mentor = settlement.getMentor();
        mentor.getId();

        List<SettlementAdjustment> adjustments =
                settlementAdjustmentRepository
                        .findApplicableAdjustments(mentor, period);

        int remainingSettlementAmount = settlement.getSettlementAmount();

        for (SettlementAdjustment adj : adjustments) {
            if (remainingSettlementAmount <= 0) break;

            int remainAdjAmount = adj.getRemainingAmount();
            if (remainAdjAmount <= 0) continue;

            int applied = Math.min(remainingSettlementAmount, remainAdjAmount);

            adj.applyPartially(applied);
            remainingSettlementAmount -= applied;
        }

        settlement.applyFinalAmount(remainingSettlementAmount);
    }

    @Transactional
    public int completeSettlementsByPeriod(YearMonth period) {

        List<Settlement> settlements =
                settlementRepository.findBySettlementPeriodAndStatus(period, SettlementStatus.PENDING);

        for (Settlement settlement : settlements) {
            calculateFinalSettlementAmount(settlement.getId());
            settlement.complete();
        }

        return settlements.size();
    }

}
