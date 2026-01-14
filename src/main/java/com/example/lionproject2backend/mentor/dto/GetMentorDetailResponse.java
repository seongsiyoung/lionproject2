package com.example.lionproject2backend.mentor.dto;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.review.domain.Review;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.domain.TutorialSkill;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 멘토 상세 조회
 */

@Getter
@AllArgsConstructor
public class GetMentorDetailResponse {

    private Long mentorId;
    private String nickname;
    private String career;
    private Integer reviewCount;
    private List<String> skills;
    private List<TutorialInfo> tutorials;
    private List<ReviewInfo> reviews;
    private LocalDateTime createdAt;

    public static GetMentorDetailResponse from(
            Mentor mentor,
            List<String> skills,
            List<Tutorial> tutorials,
            List<Review> reviews
    ) {
        List<TutorialInfo> tutorialInfos = tutorials.stream()
                .map(TutorialInfo::from)
                .toList();

        List<ReviewInfo> reviewInfos = reviews.stream()
                .map(ReviewInfo::from)
                .toList();

        return new GetMentorDetailResponse(
                mentor.getId(),
                mentor.getUser().getNickname(),
                mentor.getCareer(),
                mentor.getReviewCount(),
                skills,
                tutorialInfos,
                reviewInfos,
                mentor.getCreatedAt()
        );
    }

    @Getter
    @AllArgsConstructor
    public static class TutorialInfo {
        private Long id;
        private String title;
        private String description;
        private Integer price;
        private Integer duration;
        private BigDecimal rating;
        private String status;
        private List<SkillInfo> skills;

        public static TutorialInfo from(Tutorial tutorial) {
            List<SkillInfo> skillInfos = tutorial.getTutorialSkills().stream()
                    .map(ts -> new SkillInfo(ts.getSkill().getId(), ts.getSkill().getSkillName()))
                    .toList();

            return new TutorialInfo(
                    tutorial.getId(),
                    tutorial.getTitle(),
                    tutorial.getDescription(),
                    tutorial.getPrice(),
                    tutorial.getDuration(),
                    tutorial.getRating(),
                    tutorial.getTutorialStatus().name(),
                    skillInfos
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SkillInfo {
        private Long id;
        private String skillName;
    }

    @Getter
    @AllArgsConstructor
    public static class ReviewInfo {
        private Long id;
        private Integer rating;
        private String content;
        private MenteeInfo mentee;
        private LocalDateTime createdAt;

        public static ReviewInfo from(Review review) {
            return new ReviewInfo(
                    review.getId(),
                    review.getRating(),
                    review.getContent(),
                    new MenteeInfo(review.getMentee().getId(), review.getMentee().getNickname()),
                    review.getCreatedAt()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class MenteeInfo {
        private Long id;
        private String nickname;
    }
}
