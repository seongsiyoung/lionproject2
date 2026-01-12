package com.example.lionproject2backend.tutorial.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.skill.domain.Skill;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tutorials")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tutorial extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int price;
    private int duration;

    @Enumerated(EnumType.STRING)
    private TutorialStatus tutorialStatus;


    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(length = 20)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorialSkill> tutorialSkills = new ArrayList<>();

    /*create*/
    public static Tutorial create(
            Mentor mentor,
            String title,
            String description,
            int price,
            int duration
    ) {
        Tutorial tutorial = new Tutorial();
        tutorial.mentor = mentor;
        tutorial.title = title;
        tutorial.description = description;
        tutorial.price = price;
        tutorial.duration = duration;
        tutorial.rating = BigDecimal.ZERO;
        tutorial.tutorialStatus = TutorialStatus.ACTIVE;
        return tutorial;
    }

    public void addSkill(Skill skill) {
        TutorialSkill tutorialSkill = TutorialSkill.create(this, skill);
        this.tutorialSkills.add(tutorialSkill);
    }

    public void updateSkills(List<Skill> newSkills) {
        this.tutorialSkills.clear(); // 기존 스킬 삭제
        if (newSkills != null && !newSkills.isEmpty()) {
            addSkills(newSkills);     // 새 스킬 추가
        }
    }

    public void clearSkills() {
        this.tutorialSkills.clear(); // 스킬 리스트 빈 상태로 초기화
    }

    public void addSkills(List<Skill> skills) {
        skills.forEach(this::addSkill);
    }

}

