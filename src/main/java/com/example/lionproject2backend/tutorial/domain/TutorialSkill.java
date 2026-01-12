package com.example.lionproject2backend.tutorial.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.skill.domain.Skill;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tutorial_skills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorialSkill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id")
    private Tutorial tutorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;


    public static TutorialSkill create(
            Tutorial tutorial,
            Skill skill
    ) {
        TutorialSkill tutorialSkill = new TutorialSkill();
        tutorialSkill.tutorial = tutorial;
        tutorialSkill.skill = skill;
        return tutorialSkill;
    }
}
