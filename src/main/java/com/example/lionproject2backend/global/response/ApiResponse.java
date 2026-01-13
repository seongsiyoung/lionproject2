package com.example.lionproject2backend.global.response;


import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공했습니다.", null);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, "SUCCESS", message, null);
    }

    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        // ErrorCode enum의 실제 code(USER_001, TOKEN_003 등)로 내려주도록 통일
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
}