package com.example.lionproject2backend.payment.repository;

import com.example.lionproject2backend.payment.domain.Payment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentCustomRepository {

    @Query("select p "
            + "from Payment p "
            + "join fetch p.tutorial t "
            + "join fetch t.mentor m "
            + "join fetch m.user u "
            + "where p.id = :paymentId "
            + "and p.mentee.id = :menteeId")
    Optional<Payment> findByIdAndMenteeId(
            @Param("paymentId") Long paymentId,
            @Param("menteeId") Long menteeId
    );

    @Query("select coalesce(sum(p.amount), 0) "
            + "from Payment p "
            + "where p.mentee.id = :menteeId "
            + "and p.status = 'PAID'")
    int sumPaidAmountByMentee(Long menteeId);

    @Query("select coalesce(sum(p.amount), 0) "
            + "from Payment p "
            + "where p.mentee.id = :menteeId "
            + "and p.status = 'PAID' "
            + "and p.paidAt between :start and :end")
    int sumPaidAmountByMenteeAndPaidAtBetween(
            Long menteeId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * 특정 기간의 아직 정산되지 않은 결제 조회 (정산용)
     * - 아직 정산되지 않은 결제만 조회 (settlementDetails에 없는 payment)
     * - PAID 상태의 결제만 대상 (전체 환불만 가능하므로 부분환불 상태 없음)
     * - 멘토별 그룹화는 서비스 레이어에서 수행
     */

    @Query("select p "
            + "from Payment p "
            + "join fetch p.tutorial t "
            + "join fetch t.mentor m "
            + "where p.status = 'PAID' "
            + "and p.paidAt >= :start "
            + "and p.paidAt < :end "
            + "and not exists ( "
            + "select 1 from SettlementDetail sd where sd.payment.id = p.id) "
            + "order by m.id, p.paidAt")
    List<Payment> findUnsettledPaidPaymentsByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    /**
     * 환불 신청 목록 조회 (관리자용)
     * REFUND_REQUESTED 상태인 결제만 조회
     */
    @Query("select p "
            + "from Payment p "
            + "join fetch p.tutorial t "
            + "join fetch t.mentor m "
            + "join fetch m.user mu "
            + "join fetch p.mentee me "
            + "where p.status = 'REFUND_REQUESTED' "
            + "order by p.createdAt desc")
    List<Payment> findAllRefundRequests();

}
