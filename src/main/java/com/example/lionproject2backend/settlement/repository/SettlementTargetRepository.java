package com.example.lionproject2backend.settlement.repository;

import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementTargetRepository extends JpaRepository<SettlementTarget, Long> {

    @Query("SELECT st FROM SettlementTarget st WHERE st.jobInstanceId = :jobInstanceId AND st.status = 'READY'")
    Page<SettlementTarget> findReadyTargetsByJobInstanceId(@Param("jobInstanceId") Long jobInstanceId, Pageable pageable);

    @Query("SELECT st FROM SettlementTarget st WHERE st.jobInstanceId = :jobInstanceId")
    Page<SettlementTarget> findTargetsByJobInstanceId(@Param("jobInstanceId") Long jobInstanceId, Pageable pageable);

    List<SettlementTarget> findBySettlementPeriod(String settlementPeriod);

    List<SettlementTarget> findByJobInstanceId(Long jobInstanceId);

    Optional<SettlementTarget> findByJobInstanceIdAndMentorId(Long jobInstanceId, Long mentorId);

    @Modifying
    @Query("DELETE FROM SettlementTarget st WHERE st.jobInstanceId = :jobInstanceId")
    void deleteByJobInstanceId(@Param("jobInstanceId") Long jobInstanceId);

    @Modifying
    @Query("DELETE FROM SettlementTarget st WHERE st.settlementPeriod = :period")
    void deleteBySettlementPeriod(@Param("period") String period);

    @Modifying
    @Query("UPDATE SettlementTarget st " +
           "SET st.status = 'READY', st.jobInstanceId = :jobInstanceId " +
           "WHERE st.settlementPeriod = :period " +
           "AND st.status IN ('READY', 'FAILED', 'PROCESSING', 'SKIPPED')")
    int resetRetryableTargets(@Param("period") String period,
                              @Param("jobInstanceId") Long jobInstanceId);

    @Modifying
    @Query(value = "UPDATE settlement_targets st " +
                   "SET st.status = 'READY', st.job_instance_id = :jobInstanceId " +
                   "WHERE st.settlement_period = :period " +
                   "AND st.status = 'DONE' " +
                   "AND EXISTS ( " +
                   "    SELECT 1 " +
                   "    FROM settlements s " +
                   "    WHERE s.mentor_id = st.mentor_id " +
                   "    AND s.settlement_period = st.settlement_period " +
                   "    AND s.status = 'PENDING' " +
                   ") " +
                   "AND EXISTS ( " +
                   "    SELECT 1 " +
                   "    FROM settlement_details sd " +
                   "    INNER JOIN payments p ON sd.payment_id = p.id " +
                   "    INNER JOIN tutorials t ON p.tutorial_id = t.id " +
                   "    WHERE t.mentor_id = st.mentor_id " +
                   "    AND sd.settlement_id IS NULL " +
                   "    AND sd.occurred_at >= :startAt " +
                   "    AND sd.occurred_at < :endAt " +
                   ")", nativeQuery = true)
    int reopenPendingDoneTargets(@Param("period") String period,
                                  @Param("jobInstanceId") Long jobInstanceId,
                                  @Param("startAt") java.time.LocalDateTime startAt,
                                  @Param("endAt") java.time.LocalDateTime endAt);

    @Modifying
    @Query(value = "UPDATE settlement_targets st " +
                   "SET st.status = 'FAILED', st.job_instance_id = :jobInstanceId " +
                   "WHERE st.settlement_period = :period " +
                   "AND st.status = 'DONE' " +
                   "AND EXISTS ( " +
                   "    SELECT 1 " +
                   "    FROM settlements s " +
                   "    WHERE s.mentor_id = st.mentor_id " +
                   "    AND s.settlement_period = st.settlement_period " +
                   "    AND s.status = 'COMPLETED' " +
                   ") " +
                   "AND EXISTS ( " +
                   "    SELECT 1 " +
                   "    FROM settlement_details sd " +
                   "    INNER JOIN payments p ON sd.payment_id = p.id " +
                   "    INNER JOIN tutorials t ON p.tutorial_id = t.id " +
                   "    WHERE t.mentor_id = st.mentor_id " +
                   "    AND sd.settlement_id IS NULL " +
                   "    AND sd.occurred_at >= :startAt " +
                   "    AND sd.occurred_at < :endAt " +
                   ")", nativeQuery = true)
    int markCompletedDoneTargetsFailed(@Param("period") String period,
                                        @Param("jobInstanceId") Long jobInstanceId,
                                        @Param("startAt") java.time.LocalDateTime startAt,
                                        @Param("endAt") java.time.LocalDateTime endAt);
}
