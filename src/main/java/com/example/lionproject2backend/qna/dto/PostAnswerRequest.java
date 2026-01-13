package com.example.lionproject2backend.qna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 답변 등록 요청 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostAnswerRequest {

    @NotBlank(message = "답변 내용은 필수입니다.")
    private String content;
}
