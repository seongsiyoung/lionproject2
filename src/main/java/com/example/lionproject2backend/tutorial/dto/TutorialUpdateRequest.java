package com.example.lionproject2backend.tutorial.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class TutorialUpdateRequest {

    private String title;
    private String description;
    private int price;
    private int duration;
    private List<Long> skillIds; // DB에 있는 스킬 ID만
}