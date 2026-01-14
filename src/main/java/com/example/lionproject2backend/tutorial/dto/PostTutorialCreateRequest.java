package com.example.lionproject2backend.tutorial.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;


@Getter
@NoArgsConstructor
public class PostTutorialCreateRequest {
    private String title;
    private String description;
    private int price;
    private int duration;
    private List<String> skills;  // 스킬 이름 목록 (없으면 자동 생성)
}
