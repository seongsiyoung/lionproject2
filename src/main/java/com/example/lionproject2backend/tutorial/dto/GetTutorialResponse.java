package com.example.lionproject2backend.tutorial.dto;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.domain.TutorialStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GetTutorialResponse {

    private Long id;
    private String title;
    private String description;
    private int price;
    private int duration;
    private BigDecimal rating;
    private TutorialStatus tutorialStatus;

    private Long mentorId;

    private List<String> skills;

    public static GetTutorialResponse from(Tutorial tutorial) {
        return GetTutorialResponse.builder()
                .id(tutorial.getId())
                .title(tutorial.getTitle())
                .description(tutorial.getDescription())
                .price(tutorial.getPrice())
                .duration(tutorial.getDuration())
                .rating(tutorial.getRating())
                .tutorialStatus(tutorial.getTutorialStatus())
                .mentorId(tutorial.getMentor().getId())
                .skills(tutorial.getTutorialSkills()
                        .stream()
                        .map(ts -> ts.getSkill().getSkillName())
                        .toList())
                .build();

    }
}
