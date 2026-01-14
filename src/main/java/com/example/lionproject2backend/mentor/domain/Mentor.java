package com.example.lionproject2backend.mentor.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mentor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String career;

    @Enumerated(EnumType.STRING)
    private MentorStatus mentorStatus;

    @Column(name = "review_count")
    private int reviewCount = 0;

    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    private List<MentorSkill> mentorSkills = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    private List<Tutorial> tutorials = new ArrayList<>();

    public Mentor(User user, String career) {
        this.user = user;
        this.career = career;
        this.mentorStatus = MentorStatus.APPROVED;
        this.reviewCount = 0;
    }
}

