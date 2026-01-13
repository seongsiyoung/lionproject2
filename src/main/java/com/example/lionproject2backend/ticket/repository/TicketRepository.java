package com.example.lionproject2backend.ticket.repository;

import com.example.lionproject2backend.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // 멘티의 특정 과외 이용권 조회 (유효한 것만)
    @Query("SELECT t FROM Ticket t " +
           "WHERE t.mentee.id = :menteeId " +
           "AND t.tutorial.id = :tutorialId " +
           "AND t.remainingCount > 0 " +
           "AND (t.expiredAt IS NULL OR t.expiredAt > CURRENT_TIMESTAMP) " +
           "ORDER BY t.createdAt ASC")
    List<Ticket> findAvailableTickets(@Param("menteeId") Long menteeId,
                                       @Param("tutorialId") Long tutorialId);

    // 멘티의 전체 이용권 목록
    List<Ticket> findByMenteeIdOrderByCreatedAtDesc(Long menteeId);
}