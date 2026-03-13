package com.example.lionproject2backend.settlement.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 정산 배치 실행 시 작업 대상을 고정(Snapshot)하기 위한 테이블입니다.
 * JobInstanceId를 기준으로 재시작(Restart) 시에도 동일한 작업 셋을 유지합니다.
 */
@Entity
@Table(name = "settlement_targets", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"jobInstanceId", "mentorId", "settlementPeriod"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SettlementTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mentorId;

    @Column(nullable = false)
    private String settlementPeriod; // yyyy-MM

    @Column(nullable = false)
    private Long jobInstanceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementTargetStatus status = SettlementTargetStatus.READY;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum SettlementTargetStatus {
        READY, PROCESSING, DONE, FAILED
    }

    private SettlementTarget(Long mentorId, String settlementPeriod, Long jobInstanceId) {
        this.mentorId = mentorId;
        this.settlementPeriod = settlementPeriod;
        this.jobInstanceId = jobInstanceId;
    }

    public static SettlementTarget create(Long mentorId, String settlementPeriod, Long jobInstanceId) {
        return new SettlementTarget(mentorId, settlementPeriod, jobInstanceId);
    }

    public void markAsDone() {
        this.status = SettlementTargetStatus.DONE;
    }

    public void markAsFailed() {
        this.status = SettlementTargetStatus.FAILED;
    }

    public void markAsProcessing() {
        this.status = SettlementTargetStatus.PROCESSING;
    }
}
