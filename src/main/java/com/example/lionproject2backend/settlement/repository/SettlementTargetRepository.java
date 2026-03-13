package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SettlementTargetRepository extends JpaRepository<SettlementTarget, Long> {

    @Query("SELECT st FROM SettlementTarget st WHERE st.jobInstanceId = :jobInstanceId AND st.status = 'READY'")
    Page<SettlementTarget> findReadyTargetsByJobInstanceId(@Param("jobInstanceId") Long jobInstanceId, Pageable pageable);

    Optional<SettlementTarget> findByJobInstanceIdAndMentorId(Long jobInstanceId, Long mentorId);

    @Modifying
    @Query("DELETE FROM SettlementTarget st WHERE st.jobInstanceId = :jobInstanceId")
    void deleteByJobInstanceId(@Param("jobInstanceId") Long jobInstanceId);

    @Modifying
    @Query("DELETE FROM SettlementTarget st WHERE st.settlementPeriod = :period")
    void deleteBySettlementPeriod(@Param("period") String period);
}
