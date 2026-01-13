package com.example.lionproject2backend.qna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 질문 등록 요청  DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostQuestionRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private String codeContent;

}
