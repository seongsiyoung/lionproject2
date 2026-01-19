package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.settlement.domain.SettlementDetail;
import java.time.YearMonth;
import java.util.List;

public interface SettlementDetailCustomRepository {
    List<SettlementDetail> findUnsettledDetailsByPeriod(YearMonth settlementPeriod);
}
