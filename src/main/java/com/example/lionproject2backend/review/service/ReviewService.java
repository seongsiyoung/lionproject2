package com.example.lionproject2backend.review.service;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lesson.repository.LessonRepository;
import com.example.lionproject2backend.review.domain.Review;
import com.example.lionproject2backend.review.dto.PostReviewCreateRequest;
import com.example.lionproject2backend.review.dto.PostReviewCreateResponse;
import com.example.lionproject2backend.review.dto.PutReviewUpdateRequest;
import com.example.lionproject2backend.review.dto.GetReviewDetailResponse;
import com.example.lionproject2backend.review.repository.ReviewRepository;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.repository.TutorialRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final LessonRepository lessonRepository;
    private final TutorialRepository tutorialRepository;
    private final UserRepository userRepository;
    private static final int REVIEW_MIN_COMPLETED_LESSON_COUNT = 1;


    // 리뷰 작성
    @Transactional
    public PostReviewCreateResponse create(Long tutorialId, Long userId, PostReviewCreateRequest request) {
        Tutorial tutorial = tutorialRepository.findWithMentorById(tutorialId)
                .orElseThrow(() -> new CustomException(ErrorCode.TUTORIAL_NOT_FOUND));

        validateReviewCondition(tutorialId, userId);

        User mentee = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_FORBIDDEN));

        Review review = Review.create(
                tutorial,
                mentee,
                request.getRating(),
                request.getContent()
        );

        Review saved = reviewRepository.save(review);
        return new PostReviewCreateResponse(saved.getId());
    }

    /**
     * @param tutorialId
     * @param userId
     *
     * 유저 한명당 튜토리얼 리뷰 1개만 가능하도록 검증
     * 완료된 레슨이 1개 이상이도록 검증
     */
    private void validateReviewCondition(Long tutorialId, Long userId) {
        if (reviewRepository.existsByTutorialIdAndMenteeId(tutorialId, userId)) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        long completedCount =
                lessonRepository.countCompletedByMenteeAndTutorial(
                        userId,
                        tutorialId,
                        LessonStatus.COMPLETED
                );

        if (completedCount < REVIEW_MIN_COMPLETED_LESSON_COUNT) {
            throw new CustomException(ErrorCode.REVIEW_CREATE_NOT_ENOUGH_COMPLETED);
        }
    }

    // 리뷰 단건 조회 (튜토리얼id와 유저id를 기준으로 단건 조회)
    public GetReviewDetailResponse getMyReview(Long tutorialId, Long userId) {
        Review review = reviewRepository.findByTutorialIdAndMenteeId(tutorialId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        return GetReviewDetailResponse.from(review);
    }

    // 리뷰 리스트 조회 (공개)
    public Page<GetReviewDetailResponse> list(Long tutorialId, Pageable pageable) {
        tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new CustomException(ErrorCode.TUTORIAL_NOT_FOUND));

        return reviewRepository.findByTutorialId(tutorialId, pageable)
                .map(GetReviewDetailResponse::fromWithNickname);
    }


    // 리뷰 수정
    @Transactional
    public void update(Long tutorialId, Long reviewId, Long userId, PutReviewUpdateRequest request) {
        Review review = reviewRepository
                .findByIdAndTutorialId(reviewId, tutorialId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        validateUserAuthority(userId, review);

        review.update(request.getRating(), request.getContent());
    }

    // 리뷰 삭제 (Hard delete)
    @Transactional
    public void delete(Long tutorialId, Long reviewId, Long userId) {
        Review review = reviewRepository
                .findByIdAndTutorialId(reviewId, tutorialId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        validateUserAuthority(userId, review);

        reviewRepository.delete(review);
    }

    // 리뷰가 해당 유저가 쓴 글인지 검증
    private void validateUserAuthority(Long userId, Review review) {

        if (!review.getMentee().getId().equals(userId)) {
            throw new CustomException(ErrorCode.REVIEW_FORBIDDEN);
        }
    }
}
