package com.example.lionproject2backend.lessonfile.service;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LessonFileAccessPolicy {

    private static final Set<LessonStatus> FILE_ACCESSIBLE_STATUSES = Set.of(
            LessonStatus.CONFIRMED,
            LessonStatus.SCHEDULED,
            LessonStatus.COMPLETED
    );

    public void validateUpload(Lesson lesson, Long userId, LessonFileType type) {
        validateFileAccessibleStatus(lesson, ErrorCode.LESSON_FILE_UPLOAD_NOT_ALLOWED);
        validateAccess(lesson, userId);

        if (type == LessonFileType.MATERIAL && !lesson.isMentor(userId)) {
            throw new CustomException(ErrorCode.LESSON_FILE_FORBIDDEN);
        }

        if (type == LessonFileType.ASSIGNMENT && !lesson.isMentee(userId)) {
            throw new CustomException(ErrorCode.LESSON_FILE_FORBIDDEN);
        }
    }

    public void validateAccess(Lesson lesson, Long userId) {
        validateFileAccessibleStatus(lesson, ErrorCode.LESSON_FILE_ACCESS_NOT_ALLOWED);

        if (!lesson.isParticipant(userId)) {
            throw new CustomException(ErrorCode.LESSON_FILE_FORBIDDEN);
        }
    }

    private void validateFileAccessibleStatus(Lesson lesson, ErrorCode errorCode) {
        if (!FILE_ACCESSIBLE_STATUSES.contains(lesson.getStatus())) {
            throw new CustomException(errorCode);
        }
    }
}
