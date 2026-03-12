package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementCustomRepository {

    List<Settlement> findBySettlementPeriodAndStatus(YearMonth settlementPeriod, SettlementStatus status);
    Optional<Settlement> findByMentorAndSettlementPeriod(Mentor mentor, YearMonth settlementPeriod);

}

