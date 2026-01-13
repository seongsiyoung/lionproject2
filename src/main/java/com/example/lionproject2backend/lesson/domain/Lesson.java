package com.example.lionproject2backend.lesson.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.ticket.domain.Ticket;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lessons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LessonStatus status;

    @Column(name = "request_message", columnDefinition = "TEXT")
    private String requestMessage;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    // =============== 편의 메서드 (기존 코드 호환) =============== //

    public Tutorial getTutorial() {
        return this.ticket.getTutorial();
    }

    public User getMentee() {
        return this.ticket.getMentee();
    }

    // =============== 생성 메서드 =============== //

    public static Lesson register(Ticket ticket, String requestMessage, LocalDateTime scheduledAt) {
        validateScheduledTime(scheduledAt);

        Lesson lesson = new Lesson();
        lesson.ticket = ticket;
        lesson.requestMessage = requestMessage;
        lesson.scheduledAt = scheduledAt;
        lesson.status = LessonStatus.REQUESTED;

        // 이용권 차감
        ticket.use();

        return lesson;
    }

    private static void validateScheduledTime(LocalDateTime scheduledAt) {
        if (scheduledAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("과거 시간으로 예약할 수 없습니다");
        }
    }

    // =============== 비즈니스 로직 메서드 =============== //

    /**
     * 수업 확정 (멘토) - 기존 approve
     */
    public void confirm(Long mentorId) {
        validateMentorAuthority(mentorId);
        validateStatusTransition(LessonStatus.REQUESTED, "확정");
        this.status = LessonStatus.CONFIRMED;
    }

    /**
     * 수업 거절 (멘토)
     */
    public void reject(Long mentorId, String rejectReason) {
        validateMentorAuthority(mentorId);

        if (rejectReason == null || rejectReason.isBlank()) {
            throw new IllegalArgumentException("거절 사유를 입력해주세요");
        }

        validateStatusTransition(LessonStatus.REQUESTED, "거절");
        this.status = LessonStatus.REJECTED;
        this.rejectReason = rejectReason;

        // 이용권 복구
        this.ticket.restore();
    }

    /**
     * 수업 시작 (멘토)
     */
    public void start(Long mentorId) {
        validateMentorAuthority(mentorId);
        validateStatusTransition(LessonStatus.CONFIRMED, "시작");
        this.status = LessonStatus.IN_PROGRESS;
    }

    /**
     * 수업 완료 (멘토)
     */
    public void complete(Long mentorId) {
        validateMentorAuthority(mentorId);
        validateStatusTransition(LessonStatus.IN_PROGRESS, "완료");
        this.status = LessonStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    // =============== 검증 메서드 =============== //

    private void validateStatusTransition(LessonStatus expectedStatus, String action) {
        if (this.status != expectedStatus) {
            throw new IllegalStateException(
                    String.format("%s 상태의 수업만 %s할 수 있습니다. (현재: %s)",
                            expectedStatus.getDescription(),
                            action,
                            this.status.getDescription())
            );
        }
    }

    private void validateMentorAuthority(Long userId) {
        if (!isMentor(userId)) {
            throw new IllegalArgumentException("수업을 처리할 권한이 없습니다.");
        }
    }

    // =============== 권한 확인 메서드 =============== //

    public boolean isMentor(Long userId) {
        return this.ticket.getTutorial().getMentor().getUser().getId().equals(userId);
    }

    public boolean isMentee(Long userId) {
        return this.ticket.getMentee().getId().equals(userId);
    }

    public boolean isParticipant(Long userId) {
        return isMentee(userId) || isMentor(userId);
    }

    // =============== 상태 확인 메서드 =============== //

    public boolean isCompleted() {
        return this.status == LessonStatus.COMPLETED;
    }
}
