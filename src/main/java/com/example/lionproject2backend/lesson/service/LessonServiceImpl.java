package com.example.lionproject2backend.lesson.service;

import com.example.lionproject2backend.lesson.dto.*;
import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lesson.repository.LessonRepository;
import com.example.lionproject2backend.ticket.domain.Ticket;
import com.example.lionproject2backend.ticket.repository.TicketRepository;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final TicketRepository ticketRepository;

    /**
     * 수업 신청 (이용권 기반)
     * POST /api/tickets/{ticketId}/lessons
     */
    @Override
    @Transactional
    public PostLessonRegisterResponse register(Long ticketId, Long userId, PostLessonRegisterRequest request) {
        // 이용권 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        // 이용권 소유자 검증
        if (!ticket.getMentee().getId().equals(userId)) {
            throw new CustomException(ErrorCode.TICKET_FORBIDDEN);
        }

        // 이용권 유효성 검증
        if (!ticket.hasRemaining()) {
            throw new CustomException(ErrorCode.TICKET_EXHAUSTED);
        }

        // Lesson 생성 (내부에서 ticket.use() 호출)
        Lesson lesson = Lesson.register(
                ticket,
                request.getRequestMessage(),
                LocalDateTime.of(request.getLessonDate(), request.getLessonTime())
        );

        Lesson savedLesson = lessonRepository.save(lesson);

        return PostLessonRegisterResponse.from(savedLesson);
    }

    /**
     * 내가 신청한 수업 목록 조회 (멘티)
     */
    @Override
    public GetLessonListResponse getMyLessons(Long menteeId, LessonStatus status) {
        List<Lesson> lessons;

        if (status == null) {
            lessons = lessonRepository.findByMenteeIdWithDetails(menteeId);
        } else {
            lessons = lessonRepository.findByMenteeIdAndStatusWithDetails(menteeId, status);
        }

        return GetLessonListResponse.from(lessons);
    }

    /**
     * 수업 신청 목록 조회 (멘토)
     */
    @Override
    public GetLessonRequestListResponse getMyLessonRequests(Long mentorId, LessonStatus status) {
        List<Lesson> lessons;

        if (status == null) {
            lessons = lessonRepository.findByMentorUserIdWithDetails(mentorId);
        } else {
            lessons = lessonRepository.findByMentorUserIdAndStatusWithDetails(mentorId, status);
        }

        return GetLessonRequestListResponse.from(lessons);
    }

    /**
     * 수업 상세 조회
     */
    @Override
    public GetLessonDetailResponse getLessonDetail(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        if (!lesson.isParticipant(userId)) {
            throw new CustomException(ErrorCode.LESSON_FORBIDDEN);
        }

        return GetLessonDetailResponse.from(lesson, userId);
    }

    /**
     * 수업 확정 (멘토) - 기존 approve
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse confirm(Long lessonId, Long mentorId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        lesson.confirm(mentorId);

        return PutLessonStatusUpdateResponse.from(lesson);
    }

    /**
     * 수업 거절 (멘토) - 이용권 복구
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse reject(Long lessonId, Long mentorId, PutLessonRejectRequest request) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        // 도메인 로직 실행 (내부에서 ticket.restore() 호출)
        lesson.reject(mentorId, request.getRejectReason());

        return PutLessonStatusUpdateResponse.from(lesson);
    }

    /**
     * 수업 시작 (멘토)
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse start(Long lessonId, Long mentorId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        lesson.start(mentorId);

        return PutLessonStatusUpdateResponse.from(lesson);
    }

    /**
     * 수업 완료 (멘토)
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse complete(Long lessonId, Long mentorId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        lesson.complete(mentorId);

        return PutLessonStatusUpdateResponse.from(lesson);
    }
}
