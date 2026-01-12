package com.example.lionproject2backend.tutorial.dto;
import com.example.lionproject2backend.skill.domain.Skill;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.domain.TutorialStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@AllArgsConstructor
public class TutorialResponse {

    private Long id;
    private String title;
    private String description;
    private int price;
    private int duration;
    private BigDecimal rating;
    private TutorialStatus tutorialStatus;

    private Long mentorId;

    private List<String> skills;

    public static TutorialResponse from(Tutorial tutorial) {
        return new TutorialResponse(
                tutorial.getId(),
                tutorial.getTitle(),
                tutorial.getDescription(),
                tutorial.getPrice(),
                tutorial.getDuration(),
                tutorial.getRating(),
                tutorial.getTutorialStatus(),
                tutorial.getMentor().getId(),
                tutorial.getTutorialSkills()
                        .stream()
                        .map(ts -> ts.getSkill().getSkillName())
                        .toList()
        );
    }
}
