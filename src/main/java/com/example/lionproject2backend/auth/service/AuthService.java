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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
    private static final String REDIS_USER_RT_PREFIX = "USER_RT:";

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
        String jti = jwtUtil.getJti(refreshTokenString);

        // 1. RT:{jti} -> userId 저장
        redisTemplate.opsForValue().set(
                REDIS_RT_PREFIX + jti,
                String.valueOf(user.getId()),
                jwtProperties.getRefreshExpMs(),
                TimeUnit.MILLISECONDS
        );

        // 2. USER_RT:{userId} -> Set<jti> 인덱싱 (자가 관리를 위해 7일 TTL 설정)
        String userKey = REDIS_USER_RT_PREFIX + user.getId();
        redisTemplate.opsForSet().add(userKey, jti);
        redisTemplate.expire(userKey, 7, TimeUnit.DAYS);

        return new TokenDto(accessToken, refreshTokenString);
    }

    @Transactional
    public void logout(String refreshToken) {
        jwtUtil.validate(refreshToken);
        String jti = jwtUtil.getJti(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        // 특정 세션만 삭제
        redisTemplate.delete(REDIS_RT_PREFIX + jti);
        redisTemplate.opsForSet().remove(REDIS_USER_RT_PREFIX + userId, jti);
    }

    @Transactional
    public void logoutAll(Long userId) {
        String userKey = REDIS_USER_RT_PREFIX + userId;
        Set<String> jtis = redisTemplate.opsForSet().members(userKey);

        if (jtis != null && !jtis.isEmpty()) {
            Set<String> rtKeys = jtis.stream()
                    .map(jti -> REDIS_RT_PREFIX + jti)
                    .collect(Collectors.toSet());
            redisTemplate.delete(rtKeys);
        }
        redisTemplate.delete(userKey);
    }

    @Transactional
    public TokenDto reissue(String refreshTokenFromCookie) {
        jwtUtil.validate(refreshTokenFromCookie);
        jwtUtil.validateType(refreshTokenFromCookie, TokenType.REFRESH);

        String jti = jwtUtil.getJti(refreshTokenFromCookie);
        Long userId = jwtUtil.getUserId(refreshTokenFromCookie);

        // 1. Atomic Get-and-Delete (재발급 시 기존 JTI 즉시 무효화)
        String storedUserId = redisTemplate.opsForValue().getAndDelete(REDIS_RT_PREFIX + jti);
        
        // 2. USER_RT에서 해당 JTI 제거
        redisTemplate.opsForSet().remove(REDIS_USER_RT_PREFIX + userId, jti);

        // 3. 유효성 확인 (null이면 만료되었거나 이미 사용된 토큰)
        if (storedUserId == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccess = jwtUtil.createAccessToken(user);
        String newRefreshString = jwtUtil.createRefreshToken(user);
        String newJti = jwtUtil.getJti(newRefreshString);

        // 4. 새로운 토큰 정보 저장
        String userKey = REDIS_USER_RT_PREFIX + userId;
        redisTemplate.opsForValue().set(
                REDIS_RT_PREFIX + newJti,
                String.valueOf(userId),
                jwtProperties.getRefreshExpMs(),
                TimeUnit.MILLISECONDS
        );
        redisTemplate.opsForSet().add(userKey, newJti);
        redisTemplate.expire(userKey, 7, TimeUnit.DAYS); // 세션 인덱스 TTL 갱신

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
