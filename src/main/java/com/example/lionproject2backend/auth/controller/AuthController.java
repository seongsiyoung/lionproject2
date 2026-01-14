package com.example.lionproject2backend.auth.controller;


import com.example.lionproject2backend.auth.dto.PostAuthLoginRequest;
import com.example.lionproject2backend.auth.dto.PostAuthLoginResponse;
import com.example.lionproject2backend.auth.dto.PostDuplicateCheckResponse;
import com.example.lionproject2backend.auth.dto.TokenDto;
import com.example.lionproject2backend.auth.service.AuthService;
import com.example.lionproject2backend.auth.dto.PostAuthSignupRequest;
import com.example.lionproject2backend.auth.dto.PostAuthSignupResponse;
import com.example.lionproject2backend.auth.util.AuthCookieManager;
import com.example.lionproject2backend.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieManager authCookieManager;



    @PostMapping("/signup")
    public ApiResponse<PostAuthSignupResponse> signup(@Valid @RequestBody PostAuthSignupRequest req) {
        PostAuthSignupResponse signupResponse
                = authService.signup(req.getEmail(), req.getPassword(), req.getNickname(),req.getRole());

        return ApiResponse.success(signupResponse);
    }

    @PostMapping("/login")
    public ApiResponse<PostAuthLoginResponse> login(@RequestBody PostAuthLoginRequest req, HttpServletResponse response) {
        TokenDto tokenDto = authService.login(req.getEmail(), req.getPassword());
        authCookieManager.addRefreshToken(response, tokenDto.getRefreshToken());
        return ApiResponse.success(new PostAuthLoginResponse(tokenDto.getAccessToken()));
    }

    @PostMapping("/logout")
    public void logout(Authentication authentication, HttpServletResponse response) {
        Long userId = (Long) authentication.getPrincipal();
        authService.logout(userId);
        authCookieManager.deleteRefreshToken(response);
    }

    @PostMapping("/refresh")
    public ApiResponse<PostAuthLoginResponse> reissue(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {
        TokenDto tokenDto = authService.reissue(refreshToken);

        authCookieManager.addRefreshToken(response, tokenDto.getRefreshToken());
        return ApiResponse.success(new PostAuthLoginResponse(tokenDto.getAccessToken()));
    }

    /**
     * 이메일 중복 체크
     * GET /api/users/check-email?email=email@emaiol.com
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<PostDuplicateCheckResponse>> checkEmail(
            @RequestParam @Email String email
    ) {
        PostDuplicateCheckResponse isDuplicated = authService.checkEmail(email);
        return ResponseEntity.ok(ApiResponse.success(isDuplicated));
    }

    /**
     * 닉네임 중복 체크
     * GET /api/users/check-nickname?nickname=닉네임
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<PostDuplicateCheckResponse>> checkNickname(
            @RequestParam @NotBlank String nickname
    ) {
        PostDuplicateCheckResponse isDuplicated = authService.checkNickname(nickname);
        return ResponseEntity.ok(ApiResponse.success(isDuplicated));
    }
}
