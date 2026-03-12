package com.example.lionproject2backend.auth.service;

import com.example.lionproject2backend.auth.cookie.CookieProperties;
import com.example.lionproject2backend.auth.cookie.CookieUtil;
import com.example.lionproject2backend.auth.dto.PostAuthLoginResponse;
import com.example.lionproject2backend.auth.dto.PostAuthSignupResponse;
import com.example.lionproject2backend.auth.dto.PostDuplicateCheckResponse;
import com.example.lionproject2backend.auth.dto.TokenDto;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;
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
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    private static final String REDIS_RT_PREFIX = "RT:";

    @Transactional
    public PostAuthSignupResponse signup(String email, String rawPassword, String nickname, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encoded = passwordEncoder.encode(rawPassword);

        //ui에 롤 선택이 없어져서 코드 추가 했습니다. 추후 수정
        UserRole actualRole = (role != null) ? role : UserRole.MENTEE;
        User user = User.create(email, encoded, nickname, actualRole);

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
        String refreshTokenString = jwtUtil.createRefreshToken(user);

        // Redis에 RT:{userId}로 저장 (String Value 방식 - SETEX)
        redisTemplate.opsForValue().set(
                REDIS_RT_PREFIX + user.getId(),
                refreshTokenString,
                jwtProperties.getRefreshExpMs(),
                TimeUnit.MILLISECONDS
        );

        return new TokenDto(accessToken, refreshTokenString);
    }

    @Transactional
    public void logout(Long userId) {
        redisTemplate.delete(REDIS_RT_PREFIX + userId);
    }

    @Transactional
    public TokenDto reissue(String refreshTokenFromCookie) {

        jwtUtil.validate(refreshTokenFromCookie);
        jwtUtil.validateType(refreshTokenFromCookie, TokenType.REFRESH);

        // 1. Refresh Token에서 userId 추출
        Long userId = jwtUtil.getUserId(refreshTokenFromCookie);

        // 2. Redis에서 해당 userId의 토큰 조회 (RT:{userId})
        String storedToken = redisTemplate.opsForValue().get(REDIS_RT_PREFIX + userId);
        if (storedToken == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. 토큰 일치 여부 확인 (RTR 보안)
        if (!storedToken.equals(refreshTokenFromCookie)) {
            // 토큰이 일치하지 않으면 탈취 가능성이 있으므로 삭제 후 에러 처리
            redisTemplate.delete(REDIS_RT_PREFIX + userId);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccess = jwtUtil.createAccessToken(user);
        String newRefreshString = jwtUtil.createRefreshToken(user);

        // 4. 새로운 토큰으로 교체 (RTR) - 단순 SETEX
        redisTemplate.opsForValue().set(
                REDIS_RT_PREFIX + userId,
                newRefreshString,
                jwtProperties.getRefreshExpMs(),
                TimeUnit.MILLISECONDS
        );

        return new TokenDto(newAccess, newRefreshString);
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
