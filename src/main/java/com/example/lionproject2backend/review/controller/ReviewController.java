package com.example.lionproject2backend.review.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.review.dto.PostReviewCreateRequest;
import com.example.lionproject2backend.review.dto.PostReviewCreateResponse;
import com.example.lionproject2backend.review.dto.PutReviewUpdateRequest;
import com.example.lionproject2backend.review.dto.GetReviewDetailResponse;
import com.example.lionproject2backend.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tutorials/{tutorialId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성 (멘티)
     * POST /api/tutorials/{tutorialId}/reviews
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostReviewCreateResponse>> create(
            @PathVariable Long tutorialId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostReviewCreateRequest request
    ) {
        PostReviewCreateResponse postReviewCreateResponse = reviewService.create(tutorialId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(postReviewCreateResponse));
    }

    /**
     * 내 리뷰 단건 조회 (수정 화면용)
     * GET /api/tutorials/{tutorialId}/reviews/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetReviewDetailResponse>> getMyReview(
            @PathVariable Long tutorialId,
            @AuthenticationPrincipal Long userId
    ) {
        GetReviewDetailResponse myReview = reviewService.getMyReview(tutorialId, userId);
        return ResponseEntity.ok(ApiResponse.success(myReview));
    }

    /**
     * 리뷰 목록 조회 (공개)
     * GET /api/tutorials/{tutorialId}/reviews
     */
    @GetMapping
    public  ResponseEntity<ApiResponse<Page<GetReviewDetailResponse>>> list(
            @PathVariable Long tutorialId,
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        Page<GetReviewDetailResponse> reviewList = reviewService.list(tutorialId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviewList));
    }

    /**
     * 리뷰 수정 (멘티)
     * PATCH /api/tutorials/{tutorialId}/reviews/{reviewId}
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long tutorialId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PutReviewUpdateRequest request
    ) {
        reviewService.update(tutorialId, reviewId, userId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 리뷰 삭제 (멘티 / Hard Delete)
     * DELETE /api/tutorials/{tutorialId}/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long tutorialId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Long userId
    ) {
        reviewService.delete(tutorialId, reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
