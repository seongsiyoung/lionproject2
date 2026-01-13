package com.example.lionproject2backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 멘토 상세 조회
 */

@Getter
@AllArgsConstructor
public class GetMentorDetailResponse {

    private Long mentorId;
    private String nickname;
    private String career;
    private Integer reviewCount;
    private List<String> skills;
}
