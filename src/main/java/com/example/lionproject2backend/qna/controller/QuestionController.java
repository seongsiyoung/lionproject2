package com.example.lionproject2backend.qna.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.qna.dto.*;
import com.example.lionproject2backend.qna.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 질문 등록
     * POST /api/lessons/{lessonId}/questions
     * - 멘티만 가능
     */

    @PostMapping("/lessons/{lessonId}/questions")
    public ResponseEntity<ApiResponse<PostQuestionResponse>> postQuestion(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid PostQuestionRequest request) {

        PostQuestionResponse response = questionService.postQuestion(lessonId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 질문 목록 조회
     * GET /api/lessons/{lessonId}/questions
     * - 멘티, 멘토 조회 가능
     */

    @GetMapping("/lessons/{lessonId}/questions")
    public ResponseEntity<ApiResponse<List<GetQuestionListResponse>>> getQuestions(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId) {

        List<GetQuestionListResponse> response = questionService.getQuestions(lessonId, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 질문 상세 조회
     * GET /api/questions/{questionId}
     * - 멘티, 멘토 조회 가능
     */

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<GetQuestionDetailResponse>> getQuestion(
            @PathVariable Long questionId,
            @AuthenticationPrincipal Long userId) {

        GetQuestionDetailResponse response = questionService.getQuestion(questionId, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 답변 등록
     * POST /api/questions/{questionId}/answers
     * - 멘토만 가능
     */

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<ApiResponse<PostAnswerResponse>> postAnswer(
            @PathVariable Long questionId,
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid PostAnswerRequest request) {

        PostAnswerResponse response = questionService.postAnswer(questionId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
