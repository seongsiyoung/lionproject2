package com.example.lionproject2backend.lessonfile.service;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LessonFileAccessPolicyTest {

    private final LessonFileAccessPolicy policy = new LessonFileAccessPolicy();

    @Test
    void mentor_can_upload_material_only() {
        Lesson lesson = lesson(LessonStatus.CONFIRMED, 10L, 20L);

        policy.validateUpload(lesson, 10L, LessonFileType.MATERIAL);

        assertThatThrownBy(() -> policy.validateUpload(lesson, 10L, LessonFileType.ASSIGNMENT))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_FORBIDDEN);
    }

    @Test
    void mentee_can_upload_assignment_only() {
        Lesson lesson = lesson(LessonStatus.CONFIRMED, 10L, 20L);

        policy.validateUpload(lesson, 20L, LessonFileType.ASSIGNMENT);

        assertThatThrownBy(() -> policy.validateUpload(lesson, 20L, LessonFileType.MATERIAL))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_FORBIDDEN);
    }

    @Test
    void requested_lesson_blocks_file_access() {
        Lesson lesson = lesson(LessonStatus.REQUESTED, 10L, 20L);

        assertThatThrownBy(() -> policy.validateUpload(lesson, 20L, LessonFileType.ASSIGNMENT))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_UPLOAD_NOT_ALLOWED);
    }

    @Test
    void non_participant_cannot_access_files() {
        Lesson lesson = lesson(LessonStatus.CONFIRMED, 10L, 20L);

        assertThatThrownBy(() -> policy.validateAccess(lesson, 30L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LESSON_FILE_FORBIDDEN);
    }

    private Lesson lesson(LessonStatus status, Long mentorUserId, Long menteeId) {
        Lesson lesson = mock(Lesson.class);
        when(lesson.getStatus()).thenReturn(status);
        when(lesson.isMentor(mentorUserId)).thenReturn(true);
        when(lesson.isMentee(menteeId)).thenReturn(true);
        when(lesson.isParticipant(mentorUserId)).thenReturn(true);
        when(lesson.isParticipant(menteeId)).thenReturn(true);
        return lesson;
    }
}
