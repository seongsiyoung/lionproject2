package com.example.lionproject2backend.lesson.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 수업 거절 API
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutLessonRejectRequest {

    @NotBlank(message = "거절 사유를 입력해주세요.")
    @JsonProperty("rejectReason")
    private String rejectReason;
}
