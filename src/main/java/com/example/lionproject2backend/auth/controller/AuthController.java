package com.example.lionproject2backend.auth.controller;


import com.example.lionproject2backend.auth.dto.PostAuthLoginRequest;
import com.example.lionproject2backend.auth.dto.PostAuthLoginResponse;
import com.example.lionproject2backend.auth.service.AuthService;
import com.example.lionproject2backend.auth.dto.PostAuthSignupRequest;
import com.example.lionproject2backend.auth.dto.PostAuthSignupResponse;
import com.example.lionproject2backend.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<PostAuthSignupResponse> signup(@Valid @RequestBody PostAuthSignupRequest req) {
        PostAuthSignupResponse signupResponse
                = authService.signup(req.getEmail(), req.getPassword(), req.getNickname(),req.getRole());

        return ApiResponse.success(signupResponse);
    }

    @PostMapping("/login")
    public ApiResponse<PostAuthLoginResponse> login(@RequestBody PostAuthLoginRequest req, HttpServletResponse response) {
        PostAuthLoginResponse authLoginResponse = authService.login(req.getEmail(), req.getPassword(), response);
        return ApiResponse.success(authLoginResponse);
    }

    @PostMapping("/logout")
    public void logout(Authentication authentication, HttpServletResponse response) {
        Long userId = (Long) authentication.getPrincipal();
        authService.logout(userId, response);
    }
}
