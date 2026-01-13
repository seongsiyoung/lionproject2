package com.example.lionproject2backend.tutorial.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class PutTutorialUpdateRequest {
    private String title;
    private String description;
    private int price;
    private int duration;
    private List<Long> skillIds; // DB에 있는 스킬 ID만

}