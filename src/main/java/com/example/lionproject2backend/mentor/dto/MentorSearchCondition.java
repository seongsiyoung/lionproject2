package com.example.lionproject2backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 멘토 검색 조건
 * Controller에서 @ModelAttribute로 받아서 사용해주기
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorSearchCondition {
    private List<String> skills;

    //멘토 닉네임
    private String nickname;

    //최소 가격
    private Integer minPrice;

    //최대 가격
    private Integer maxPrice;

    //최소 평점
    private Double minRating;

    /**
     * 정렬 기준
     * - rating: 평점 높은순
     * - reviewCount: 리뷰 많은순
     * - priceAsc: 낮은 가격순
     * - priceDesc: 가격 높은순
     */
    private String sortBy;
}
