package com.example.lionproject2backend.lesson.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LessonStatus {

    REQUESTED("요청됨"),
    CONFIRMED("확정됨"),
    REJECTED("거절됨"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료됨"),
    CANCELLED("취소됨");

    private final String description;
}
