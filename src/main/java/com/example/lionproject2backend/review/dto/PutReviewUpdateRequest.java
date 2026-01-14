package com.example.lionproject2backend.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PutReviewUpdateRequest {

    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    @Size(max = 500)
    private String content;
}
