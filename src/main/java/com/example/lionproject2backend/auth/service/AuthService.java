package com.example.lionproject2backend.auth.service;

import com.example.lionproject2backend.auth.cookie.CookieProperties;
import com.example.lionproject2backend.auth.cookie.CookieUtil;
import com.example.lionproject2backend.auth.domain.RefreshTokenStorage;
import com.example.lionproject2backend.auth.dto.PostAuthLoginResponse;
import com.example.lionproject2backend.auth.dto.PostAuthSignupResponse;
import com.example.lionproject2backend.auth.repository.RefreshTokenStorageRepository;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.global.security.jwt.JwtUtil;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import com.example.lionproject2backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProps;
    private final RefreshTokenStorageRepository refreshRepo;

    private static final long REFRESH_COOKIE_MAX_AGE_SECONDS = 60L * 60 * 24 * 7;

    @Transactional
    public PostAuthSignupResponse signup(String email, String rawPassword, String nickname, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encoded = passwordEncoder.encode(rawPassword);
        User user = User.create(email, encoded, nickname, role);

        User savedUser = userRepository.save(user);

        return PostAuthSignupResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .role(savedUser.getUserRole().name())
                .build();
    }

    @Transactional
    public PostAuthLoginResponse login(String email, String password, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        Long userId = (Long) auth.getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        refreshRepo.findByUser(user)
                .ifPresentOrElse(
                        refreshTokenStorage -> refreshTokenStorage.update(refreshToken),
                        () -> refreshRepo.save(RefreshTokenStorage.create(user, refreshToken))
                );

        response.addHeader(HttpHeaders.SET_COOKIE,
                CookieUtil.createRefreshCookie(cookieProps, refreshToken, REFRESH_COOKIE_MAX_AGE_SECONDS).toString()
        );

        return new PostAuthLoginResponse(accessToken);
    }

    @Transactional
    public void logout(Long userId, HttpServletResponse response) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshRepo.deleteByUser(user);

        response.addHeader(HttpHeaders.SET_COOKIE,
                CookieUtil.deleteRefreshCookie(cookieProps).toString()
        );
    }
}
