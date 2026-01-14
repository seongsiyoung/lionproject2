package com.example.lionproject2backend.review.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_review_mentee_tutorial", columnNames = {"mentee_id", "tutorial_id"})
        }
        // indexes = {
        //         @Index(name = "idx_review_tutorial", columnList = "tutorial_id"),
        //         @Index(name = "idx_review_mentor", columnList = "mentor_id")
        // }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id")
    private Tutorial tutorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id")
    private User mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Review(Tutorial tutorial, User mentee, Mentor mentor, int rating, String content){
        this.tutorial = tutorial;
        this.mentee = mentee;
        this.mentor = mentor;
        this.rating = rating;
        this.content = content;
    }

    // 리뷰 생성
    public static Review create(
            Tutorial tutorial,
            User mentee,
            int rating,
            String content
    ) {
        return Review.builder()
                .tutorial(tutorial)
                .mentee(mentee)
                .mentor(tutorial.getMentor())
                .rating(rating)
                .content(content)
                .build();
    }

    // 리뷰 수정
    public void update(int rating, String content) {
        this.rating = rating;
        this.content = content;
    }

}
