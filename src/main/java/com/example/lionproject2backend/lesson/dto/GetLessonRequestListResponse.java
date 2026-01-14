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
 * Get /api/lessons/requests - 수업 신청 목록 (멘토)
 *
 * Query Parameter
 * status: PENDING, APPROVED, REJECTED, etc
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GetLessonRequestListResponse {

    private List<LessonRequestItem> lessons;

    public static GetLessonRequestListResponse from(List<Lesson> lessonList) {
        List<LessonRequestItem> list = lessonList.stream()
                .map(LessonRequestItem::from)
                .collect(Collectors.toList());

        return GetLessonRequestListResponse.builder()
                .lessons(list)
                .build();
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    public static class LessonRequestItem {
        private Long lessonId;
        private Long tutorialId;
        private String tutorialTitle;
        private String menteeName;
        private String menteeEmail;
        private LocalDateTime scheduledAt;
        private String requestMessage;
        private LessonStatus status;
        private LocalDateTime createdAt;

        public static LessonRequestItem from(Lesson lesson) {
            return LessonRequestItem.builder()
                    .lessonId(lesson.getId())
                    .tutorialId(lesson.getTutorial().getId())
                    .tutorialTitle(lesson.getTutorial().getTitle())
                    .menteeName(lesson.getMentee().getNickname())
                    .menteeEmail(lesson.getMentee().getEmail())
                    .scheduledAt(lesson.getScheduledAt())
                    .requestMessage(lesson.getRequestMessage())
                    .status(lesson.getStatus())
                    .createdAt(lesson.getCreatedAt())
                    .build();
        }
    }

}
