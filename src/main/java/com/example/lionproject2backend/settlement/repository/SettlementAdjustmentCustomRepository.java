package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.settlement.domain.SettlementAdjustment;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementAdjustmentCustomRepository {

    List<SettlementAdjustment> findApplicableAdjustments(Mentor mentor, YearMonth settlementPeriod);

}
