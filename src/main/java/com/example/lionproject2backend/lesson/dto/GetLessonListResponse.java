package com.example.lionproject2backend.lesson.dto;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Get /api/lessons/my - 내가 신청한 수업 목록 (멘티)
 *
 * Query Parameter
 * status: REQUESTED, CONFIRMED, REJECTED, SCHEDULED, COMPLETED, CANCELLED
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GetLessonListResponse {

    private List<MyLessonItem> lessons;

    //Lesson 리스트 -> GetLessonListResponse DTO 로 변환
    public static GetLessonListResponse from(List<Lesson> lessons) {
        List<MyLessonItem> list = lessons.stream()
                .map(MyLessonItem::from)
                .collect(Collectors.toList());

        return GetLessonListResponse.builder()
                .lessons(list)
                .build();
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    public static class MyLessonItem {
        private Long lessonId;
        private Long ticketId;
        private Long tutorialId;
        private String tutorialTitle;
        private String mentorName;
        private LocalDateTime scheduledAt;
        private LessonStatus status;
        private LocalDateTime createdAt;

        // Lesson 엔티티 -> MyLessonItem 으로 변환
        public static MyLessonItem from(Lesson lesson) {
            return MyLessonItem.builder()
                    .lessonId(lesson.getId())
                    .ticketId(lesson.getTicket().getId())
                    .tutorialId(lesson.getTutorial().getId())
                    .tutorialTitle(lesson.getTutorial().getTitle())
                    .mentorName(lesson.getTutorial().getMentor().getUser().getNickname())
                    .scheduledAt(lesson.getScheduledAt())
                    .status(lesson.getStatus())
                    .createdAt(lesson.getCreatedAt())
                    .build();
        }
    }
}
