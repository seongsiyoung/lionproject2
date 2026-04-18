package com.example.lionproject2backend.lessonfile.service.exception;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;

public class LessonFileConfirmException extends CustomException {

    public LessonFileConfirmException(ErrorCode errorCode) {
        super(errorCode);
    }
}
