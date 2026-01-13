package com.example.lionproject2backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 멘토 목록 조회
 */

@Getter
@AllArgsConstructor
public class GetMentorListResponse {
    private Long mentorId;
    private String nickname;
    private String career;
    private Integer reviewCount;
    private List<String> skills;
}