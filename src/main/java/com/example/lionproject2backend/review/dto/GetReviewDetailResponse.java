package com.example.lionproject2backend.review.dto;

import com.example.lionproject2backend.review.domain.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetReviewDetailResponse {
    private Long id;
    private int rating;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    public static GetReviewDetailResponse from(Review review) {
        return GetReviewDetailResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static GetReviewDetailResponse fromWithNickname(Review review) {
        return GetReviewDetailResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .nickname(review.getMentee().getNickname())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}