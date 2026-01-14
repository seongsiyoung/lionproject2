package com.example.lionproject2backend.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Profile("local")
@Component
@RequiredArgsConstructor
public class LocalBypassFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 인증 엔드포인트는 스킵
        if (request.getRequestURI().startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더가 있으면 JWT 필터에서 처리하도록 스킵
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더가 없는 경우에만 테스트용 인증 설정
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        1L,
                        null,
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_MENTEE"
                        ))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}

