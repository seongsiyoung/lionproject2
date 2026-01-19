package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.settlement.domain.SettlementAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementAdjustmentRepository extends JpaRepository<SettlementAdjustment, Long>, SettlementAdjustmentCustomRepository {
}
