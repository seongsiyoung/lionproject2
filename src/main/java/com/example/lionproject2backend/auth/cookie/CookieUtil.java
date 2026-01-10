package com.example.lionproject2backend.auth.cookie;

import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static ResponseCookie createRefreshCookie(
            CookieProperties props,
            String refreshToken,
            long maxAgeSeconds) {
        return ResponseCookie.from(props.getRefreshName(), refreshToken)
                .httpOnly(true)
                .secure(props.isRefreshSecure())
                .path(props.getRefreshPath())
                .sameSite(props.getRefreshSameSite())
                .maxAge(maxAgeSeconds)
                .build();
    }

    public static ResponseCookie deleteRefreshCookie(CookieProperties props) {
        return ResponseCookie.from(props.getRefreshName(), "")
                .httpOnly(true)
                .secure(props.isRefreshSecure())
                .path(props.getRefreshPath())
                .sameSite(props.getRefreshSameSite())
                .maxAge(0)
                .build();
    }
}