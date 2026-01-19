package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementCustomRepository {

    List<Settlement> findBySettlementPeriodAndStatus(YearMonth settlementPeriod, SettlementStatus status);

}
