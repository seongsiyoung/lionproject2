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
    private List<Long> skillIds;
}
