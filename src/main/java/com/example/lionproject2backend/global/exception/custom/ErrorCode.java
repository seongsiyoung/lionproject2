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

	// TOKEN
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_001", "토큰이 만료되었습니다."),
	TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_002", "유효하지 않은 토큰입니다."),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_003", "토큰이 존재하지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_004", "리프레시 토큰이 올바르지 않습니다."),

	// MENTOR
	MENTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "MENTOR_001", "멘토를 찾을 수 없습니다."),
	MENTOR_FORBIDDEN(HttpStatus.FORBIDDEN, "MENTOR_002", "멘토 권한이 필요합니다."),
	ALREADY_MENTOR(HttpStatus.CONFLICT, "MENTOR_003", "이미 멘토로 등록되어 있습니다."),

	// TUTORIAL
	TUTORIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "TUTORIAL_001", "튜토리얼을 찾을 수 없습니다."),
	TUTORIAL_FORBIDDEN(HttpStatus.FORBIDDEN, "TUTORIAL_002", "해당 튜토리얼에 대한 권한이 없습니다."),

	// TICKET
	TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKET_001","티켓 정보를 찾을 수 없습니다. 관리자에게 문의 부탁드립니다"),
	TICKET_FORBIDDEN(HttpStatus.FORBIDDEN, "TICKET_002", "해당 티켓에 대한 권한이 없습니다."),
	TICKET_EXHAUSTED(HttpStatus.BAD_REQUEST, "TICKET_003", "티켓 잔여 횟수가 부족합니다."),

	// LESSON
	LESSON_NOT_FOUND(HttpStatus.NOT_FOUND, "LESSON_001", "수업을 찾을 수 없습니다."),
	LESSON_FORBIDDEN(HttpStatus.FORBIDDEN, "LESSON_002", "해당 수업에 대한 권한이 없습니다."),
	LESSON_INVALID_STATUS(HttpStatus.BAD_REQUEST, "LESSON_003", "수업 상태가 올바르지 않습니다."),
	LESSON_PAST_DATE(HttpStatus.BAD_REQUEST, "LESSON_004", "과거 날짜로는 수업을 신청할 수 없습니다."),

	// REVIEW
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001", "리뷰를 찾을 수 없습니다."),
	REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "REVIEW_002","이미 해당 튜토리얼에 리뷰가 존재합니다."),
	REVIEW_CREATE_NOT_ENOUGH_COMPLETED(HttpStatus.BAD_REQUEST, "REVIEW_003","리뷰 작성은 최소 수강 완료 횟수 이후 가능합니다."),
	REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "REVIEW_004", "본인이 작성한 리뷰만 접근할 수 있습니다."),

	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_001","결제 정보를 찾을 수 없습니다."),
	PAYMENT_INVALID_STATUS(HttpStatus.BAD_REQUEST, "PAYMENT_002", "결제 상태가 올바르지 않습니다."),
	PAYMENT_CANNOT_COMPLETE(HttpStatus.BAD_REQUEST, "PAYMENT_003", "대기중인 결제만 완료할 수 있습니다."),
	PAYMENT_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "PAYMENT_004", "완료된 결제는 취소할 수 없습니다."),
	PAYMENT_CANNOT_REQUEST_REFUND(HttpStatus.BAD_REQUEST, "PAYMENT_005", "완료된 결제만 환불 신청할 수 있습니다."),
	PAYMENT_CANNOT_REJECT_REFUND(HttpStatus.BAD_REQUEST, "PAYMENT_006", "환불 신청된 결제만 거절할 수 있습니다."),
	PAYMENT_CANNOT_CANCEL_REFUND_REQUEST(HttpStatus.BAD_REQUEST, "PAYMENT_007", "환불 신청된 결제만 취소할 수 있습니다."),
	PAYMENT_CANNOT_PROCESS_REFUND(HttpStatus.BAD_REQUEST, "PAYMENT_008", "환불 신청된 결제만 환불 처리할 수 있습니다."),
	PAYMENT_INVALID_REFUND_AMOUNT(HttpStatus.BAD_REQUEST, "PAYMENT_009", "전체 환불만 가능합니다."),
	PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "PAYMENT_010", "결제가 완료되지 않았습니다."),
	PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT_011", "결제 금액이 일치하지 않습니다."),
	PAYMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "PAYMENT_012", "본인의 결제 건만 접근할 수 있습니다."),
	PAYMENT_ADMIN_REQUIRED(HttpStatus.FORBIDDEN, "PAYMENT_013", "관리자만 접근할 수 있습니다."),

	SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SETTLEMENT_001","정산 정보를 찾을 수 없습니다."),
	SETTLEMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "SETTLEMENT_002","이미 해당 기간의 정산이 존재합니다."),
	SETTLEMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "SETTLEMENT_003","이미 지급 완료된 정산입니다."),
	SETTLEMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "SETTLEMENT_004","본인의 정산 정보만 조회할 수 있습니다."),
	SETTLEMENT_DETAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "SETTLEMENT_005","해당 결제에 대한 정산 상세 정보를 찾을 수 없습니다."),


	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}

