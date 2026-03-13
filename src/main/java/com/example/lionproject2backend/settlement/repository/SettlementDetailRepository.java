package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import com.example.lionproject2backend.settlement.domain.SettlementDetailType;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SettlementDetailRepository extends JpaRepository<SettlementDetail, Long>, SettlementDetailCustomRepository {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE settlement_details sd " +
                   "INNER JOIN payments p ON sd.payment_id = p.id " +
                   "INNER JOIN tutorials t ON p.tutorial_id = t.id " +
                   "SET sd.settlement_id = :settlementId " +
                   "WHERE t.mentor_id = :mentorId " +
                   "AND sd.occurred_at >= :startAt " +
                   "AND sd.occurred_at < :endAt " +
                   "AND sd.settlement_id IS NULL", nativeQuery = true)
    int bulkUpdateSettlementId(@Param("settlementId") Long settlementId,
                               @Param("mentorId") Long mentorId,
                               @Param("startAt") LocalDateTime startAt,
                               @Param("endAt") LocalDateTime endAt);

    @Query(value = "SELECT new com.example.lionproject2backend.settlement.dto.SettlementAggregationRow(" +
                   "m.id, " +
                   "SUM(CASE WHEN sd.type = 'PAYMENT' THEN sd.paymentAmount ELSE 0 END), " +
                   "SUM(CASE WHEN sd.type = 'REFUND' THEN ABS(sd.paymentAmount) ELSE 0 END), " +
                   "SUM(sd.platformFee), " +
                   "SUM(sd.settlementAmount)) " +
                   "FROM SettlementDetail sd " +
                   "JOIN sd.payment p " +
                   "JOIN p.tutorial t " +
                   "JOIN t.mentor m " +
                   "WHERE sd.settlement IS NULL " +
                   "AND sd.occurredAt >= :startAt " +
                   "AND sd.occurredAt < :endAt " +
                   "GROUP BY m.id",
           countQuery = "SELECT count(DISTINCT m.id) " +
                   "FROM SettlementDetail sd " +
                   "JOIN sd.payment p " +
                   "JOIN p.tutorial t " +
                   "JOIN t.mentor m " +
                   "WHERE sd.settlement IS NULL " +
                   "AND sd.occurredAt >= :startAt " +
                   "AND sd.occurredAt < :endAt")
    Page<SettlementAggregationRow> findSettlementAggregationByPeriod(
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            Pageable pageable);

    List<SettlementDetail> findBySettlementIdOrderByCreatedAtDesc(Long settlementId);
    Optional<SettlementDetail> findByPayment(Payment payment);
    boolean existsByPaymentAndType(Payment payment, SettlementDetailType type);

}
