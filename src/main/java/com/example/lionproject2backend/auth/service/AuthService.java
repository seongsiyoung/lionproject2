package com.example.lionproject2backend.auth.service;

import com.example.lionproject2backend.auth.cookie.CookieProperties;
import com.example.lionproject2backend.auth.cookie.CookieUtil;
import com.example.lionproject2backend.auth.domain.RefreshTokenStorage;
import com.example.lionproject2backend.auth.dto.PostAuthLoginResponse;
import com.example.lionproject2backend.auth.dto.PostAuthSignupResponse;
import com.example.lionproject2backend.auth.dto.PostDuplicateCheckResponse;
import com.example.lionproject2backend.auth.dto.TokenDto;
import com.example.lionproject2backend.auth.repository.RefreshTokenStorageRepository;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.global.security.jwt.JwtProperties;
import com.example.lionproject2backend.global.security.jwt.JwtUtil;
import com.example.lionproject2backend.global.security.jwt.TokenType;
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
    private final RefreshTokenStorageRepository refreshRepo;

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

        return PostAuthSignupResponse.from(savedUser);
    }

    @Transactional
    public TokenDto login(String email, String password) {
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

        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshRepo.deleteByUser(user);
    }

    @Transactional
    public TokenDto reissue(String refreshTokenFromCookie) {

        jwtUtil.validate(refreshTokenFromCookie);
        jwtUtil.validateType(refreshTokenFromCookie, TokenType.REFRESH);

        RefreshTokenStorage refreshTokenStorage = refreshRepo.findByRefreshToken(refreshTokenFromCookie)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        User user = refreshTokenStorage.getUser();

        String newAccess = jwtUtil.createAccessToken(user);
        String newRefresh = jwtUtil.createRefreshToken(user);

        refreshTokenStorage.update(newRefresh);

        return new TokenDto(newAccess, newRefresh);
    }

    /**
     * 이메일 중복 체크
     */
    public PostDuplicateCheckResponse checkEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return new PostDuplicateCheckResponse(exists);
    }

    /**
     * 닉네임 중복 체크
     */
    public PostDuplicateCheckResponse checkNickname(String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);
        return new PostDuplicateCheckResponse(exists);
    }
}
