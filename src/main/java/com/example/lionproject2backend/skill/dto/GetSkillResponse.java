package com.example.lionproject2backend.skill.dto;

import com.example.lionproject2backend.skill.domain.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetSkillResponse {
    private Long id;
    private String name;

    public static GetSkillResponse from(Skill skill) {
        return new GetSkillResponse(skill.getId(), skill.getSkillName());
    }
}
