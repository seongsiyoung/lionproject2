package com.example.lionproject2backend.lessonfile.repository;

import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.domain.LessonFileStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonFileRepository extends JpaRepository<LessonFile, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from LessonFile f " +
            "join fetch f.lesson l " +
            "join fetch l.ticket tk " +
            "join fetch tk.tutorial t " +
            "join fetch t.mentor m " +
            "join fetch m.user " +
            "join fetch tk.mentee " +
            "join fetch f.uploader " +
            "where f.id = :fileId")
    Optional<LessonFile> findByIdForUpdate(@Param("fileId") Long fileId);

    long countByLessonIdAndTypeAndStatusIn(
            Long lessonId,
            LessonFileType type,
            Collection<LessonFileStatus> statuses
    );

    @Query("select f from LessonFile f " +
            "join fetch f.uploader " +
            "where f.lesson.id = :lessonId " +
            "and f.status = :status " +
            "order by f.createdAt desc")
    List<LessonFile> findByLessonIdAndStatusOrderByCreatedAtDesc(
            @Param("lessonId") Long lessonId,
            @Param("status") LessonFileStatus status
    );

    @Query("select f from LessonFile f " +
            "where f.status = com.example.lionproject2backend.lessonfile.domain.LessonFileStatus.PENDING " +
            "and f.expiresAt < :now")
    List<LessonFile> findExpiredPendingFiles(@Param("now") LocalDateTime now);
}
