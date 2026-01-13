package com.example.lionproject2backend.lesson.repository;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * 멘티가 신청한 수업 목록 (모든 상태)
     */
    @Query("SELECT l FROM Lesson l " +
            "JOIN FETCH l.ticket tk " +
            "JOIN FETCH tk.tutorial t " +
            "JOIN FETCH t.mentor m " +
            "JOIN FETCH m.user " +
            "WHERE tk.mentee.id = :menteeId " +
            "ORDER BY l.createdAt DESC")
    List<Lesson> findByMenteeIdWithDetails(@Param("menteeId") Long menteeId);

    /**
     * 멘티가 신청한 수업 목록 (상태 필터)
     */
    @Query("SELECT l FROM Lesson l " +
            "JOIN FETCH l.ticket tk " +
            "JOIN FETCH tk.tutorial t " +
            "JOIN FETCH t.mentor m " +
            "JOIN FETCH m.user " +
            "WHERE tk.mentee.id = :menteeId " +
            "AND l.status = :status " +
            "ORDER BY l.createdAt DESC")
    List<Lesson> findByMenteeIdAndStatusWithDetails(
            @Param("menteeId") Long menteeId,
            @Param("status") LessonStatus status);

    /**
     * 멘토에게 온 수업 신청 목록 (모든 상태)
     */
    @Query("SELECT l FROM Lesson l " +
            "JOIN FETCH l.ticket tk " +
            "JOIN FETCH tk.tutorial t " +
            "JOIN FETCH tk.mentee " +
            "WHERE t.mentor.user.id = :userId " +
            "ORDER BY l.createdAt DESC")
    List<Lesson> findByMentorUserIdWithDetails(@Param("userId") Long userId);

    /**
     * 멘토에게 온 수업 신청 목록 (상태 필터)
     */
    @Query("SELECT l FROM Lesson l " +
            "JOIN FETCH l.ticket tk " +
            "JOIN FETCH tk.tutorial t " +
            "JOIN FETCH tk.mentee " +
            "WHERE t.mentor.user.id = :userId " +
            "AND l.status = :status " +
            "ORDER BY l.createdAt DESC")
    List<Lesson> findByMentorUserIdAndStatusWithDetails(
            @Param("userId") Long userId,
            @Param("status") LessonStatus status);

    /**
     * 수업 상세 조회 (모든 연관 엔티티 fetch)
     */
    @Query("SELECT l FROM Lesson l " +
            "JOIN FETCH l.ticket tk " +
            "JOIN FETCH tk.tutorial t " +
            "JOIN FETCH t.mentor m " +
            "JOIN FETCH m.user " +
            "JOIN FETCH tk.mentee " +
            "WHERE l.id = :lessonId")
    Optional<Lesson> findByIdWithDetails(@Param("lessonId") Long lessonId);
}
