package com.example.lionproject2backend.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 질문 목록 조회 응답 DTO
 */

@Getter
@AllArgsConstructor
public class GetQuestionListResponse {
    private Long questionId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
