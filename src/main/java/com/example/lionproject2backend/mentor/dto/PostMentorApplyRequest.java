package com.example.lionproject2backend.mentor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostMentorApplyRequest {

    @NotEmpty(message = "스킬은 최소 1개 이상 등록해야 합니다.")
    private List<String> skills;

    @NotBlank(message = "경력은 필수 항목 입니다.")
    private String career;
}
