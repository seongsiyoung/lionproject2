package com.example.lionproject2backend.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  질문 상세 조회 응답 DTO
 */

@Getter
@AllArgsConstructor
public class GetQuestionDetailResponse {
    private Long questionId;
    private String title;
    private String content;
    private String codeContent;
    private LocalDateTime createdAt;
    private List<AnswerDto> answers;

    /**
     * 질문 상세 조회 시 답변 목록
     */

    @Getter
    @AllArgsConstructor
    public static class AnswerDto {
        private Long answerId;
        private String content;
        private LocalDateTime createdAt;
    }
}
