package com.example.lionproject2backend.lesson.service;

import com.example.lionproject2backend.lesson.dto.*;
import com.example.lionproject2backend.lesson.domain.LessonStatus;

public interface LessonService {

    PostLessonRegisterResponse register(Long ticketId, Long userId, PostLessonRegisterRequest request);
    GetLessonListResponse getMyLessons(Long menteeId, LessonStatus status);
    GetLessonRequestListResponse getMyLessonRequests(Long mentorId, LessonStatus status);
    GetLessonDetailResponse getLessonDetail(Long lessonId, Long userId);
    PutLessonStatusUpdateResponse confirm(Long lessonId, Long mentorId);
    PutLessonStatusUpdateResponse reject(Long lessonId, Long mentorId, PutLessonRejectRequest request);
    PutLessonStatusUpdateResponse start(Long lessonId, Long mentorId);
    PutLessonStatusUpdateResponse complete(Long lessonId, Long mentorId);
}
