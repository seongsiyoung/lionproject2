package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettlementDetailRepository extends JpaRepository<SettlementDetail, Long>, SettlementDetailCustomRepository {

    List<SettlementDetail> findBySettlementIdOrderByCreatedAtDesc(Long settlementId);
    Optional<SettlementDetail> findByPayment(Payment payment);

}
