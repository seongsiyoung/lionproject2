package com.example.lionproject2backend.lesson.controller;

import com.example.lionproject2backend.lesson.dto.*;
import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lesson.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


/**
 * 수업 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    /**
     * 수업 신청 (이용권 기반) - 인증 필요
     * Post /api/tickets/{ticketId}/lessons
     */
    @PostMapping("/tickets/{ticketId}/lessons")
    public ResponseEntity<ApiResponse<PostLessonRegisterResponse>> registerLesson(
            @PathVariable Long ticketId,
            @Valid @RequestBody PostLessonRegisterRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        PostLessonRegisterResponse response = lessonService.register(ticketId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내가 신청한 수업 목록 조회 (멘티) - 인증 필요
     * Get /api/lessons/my(?status=REQUESTED)
     * status - REQUESTED, CONFIRMED, REJECTED, IN_PROGRESS, COMPLETED, CANCELLED
     */
    @GetMapping("/lessons/my")
    public ResponseEntity<ApiResponse<GetLessonListResponse>> getMyLessons(
            @RequestParam(required = false) LessonStatus status,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonListResponse myLessons = lessonService.getMyLessons(userId, status);
        return ResponseEntity.ok(ApiResponse.success(myLessons));
    }

    /**
     * 수업 신청 목록 조회 (멘토) - 인증 필요
     * Get /api/lessons/requests(?status=REQUESTED)
     */
    @GetMapping("/lessons/requests")
    public ResponseEntity<ApiResponse<GetLessonRequestListResponse>> getLessonRequests(
            @RequestParam(required = false) LessonStatus status,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonRequestListResponse response = lessonService.getMyLessonRequests(userId, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 수업 상세 조회
     * Get /api/lessons/{lessonId}
     */
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<GetLessonDetailResponse>> getLessonDetail(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonDetailResponse response = lessonService.getLessonDetail(lessonId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 수업 확정 (멘토)
     * PUT /api/lessons/{lessonId}/confirm
     */
    @PutMapping("/lessons/{lessonId}/confirm")
    public ResponseEntity<ApiResponse<PutLessonStatusUpdateResponse>> confirmLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId
    ) {
        PutLessonStatusUpdateResponse response = lessonService.confirm(lessonId, userId);
        return ResponseEntity.ok(ApiResponse.success("수업이 확정되었습니다.", response));
    }

    /**
     * 수업 거절 (멘토)
     * Put /api/lessons/{lessonId}/reject
     */
    @PutMapping("/lessons/{lessonId}/reject")
    public ResponseEntity<ApiResponse<PutLessonStatusUpdateResponse>> rejectLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PutLessonRejectRequest request
    ) {
        PutLessonStatusUpdateResponse response = lessonService.reject(lessonId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("수업이 거절되었습니다.", response));
    }

    /**
     * 수업 시작
     * Put /api/lessons/{lessonId}/start
     */
    @PutMapping("/lessons/{lessonId}/start")
    public ResponseEntity<ApiResponse<PutLessonStatusUpdateResponse>> startLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId
    ) {
        PutLessonStatusUpdateResponse response = lessonService.start(lessonId, userId);
        return ResponseEntity.ok(ApiResponse.success("수업이 시작되었습니다.",response));
    }

    /**
     * 수업 완료
     * Put /api/lessons/{lessonId}/complete
     */
    @PutMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<ApiResponse<PutLessonStatusUpdateResponse>> completeLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId
    ) {
        PutLessonStatusUpdateResponse response = lessonService.complete(lessonId, userId);
        return ResponseEntity.ok(ApiResponse.success("수업이 완료되었습니다.",response));
    }


}
