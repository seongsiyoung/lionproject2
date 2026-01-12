package com.example.lionproject2backend.tutorial.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TutorialCreateRequest {

    private Long mentorId;
    private String title;
    private String description;
    private int price;
    private int duration;

    private List<Long> skillIds;

}
