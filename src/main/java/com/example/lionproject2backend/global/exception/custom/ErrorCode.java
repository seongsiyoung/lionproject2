package com.example.lionproject2backend.global.exception.custom;

import com.example.lionproject2backend.tutorial.domain.Tutorial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// COMMON
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-001", "서버 내부 오류가 발생했습니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-002", "요청 값이 올바르지 않습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-003", "지원하지 않는 HTTP 메서드입니다."),
	INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "COMMON-004", "요청 본문(JSON) 형식이 올바르지 않습니다."),

	// AUTH
	INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_002", "접근 권한이 없습니다."),
	AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "인증이 필요합니다."),

	// USER
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_001", "이미 사용 중인 이메일입니다."),
	DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER_002", "이미 사용 중인 닉네임입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_003", "존재하지 않는 사용자입니다."),
	INVALID_CREDENTIAL(HttpStatus.BAD_REQUEST,"USER_004", "ID/비밀번호가 올바르지 않습니다."),

	// Logout
	LOGOUT_DONE(HttpStatus.UNAUTHORIZED, "LOGOUT_001", "로그아웃 되었습니다."),

	// TOKEN
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_001", "토큰이 만료되었습니다."),
	TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_002", "유효하지 않은 토큰입니다."),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_003", "토큰이 존재하지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_004", "리프레시 토큰이 올바르지 않습니다."),

	TUTORIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "TUTORIAL_001","튜토리얼을 찾을 수 없습니다."),

	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001","리뷰를 찾을 수 없습니다."),
	REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "REVIEW_002","이미 해당 튜토리얼에 리뷰가 존재합니다."),
	REVIEW_CREATE_NOT_ENOUGH_COMPLETED(HttpStatus.BAD_REQUEST, "REVIEW_003","리뷰 작성은 최소 수강 완료 횟수 이후 가능합니다."),
	REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "REVIEW_004","본인이 작성한 리뷰만 접근할 수 있습니다.");

	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}

