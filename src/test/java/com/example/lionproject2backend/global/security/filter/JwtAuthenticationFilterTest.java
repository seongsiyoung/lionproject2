package com.example.lionproject2backend.global.security.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.lionproject2backend.global.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtUtil, new ObjectMapper());
    }

    @Test
    void postRefreshSkipsAccessTokenValidationEvenWithExpiredAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/refresh");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer expired-access-token");

        filter.doFilter(request, response, filterChain);

        verify(jwtUtil, never()).validate(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void getRefreshDoesNotSkipAccessTokenValidation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/auth/refresh");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer expired-access-token");
        doThrow(new ExpiredJwtException(null, null, "expired"))
                .when(jwtUtil)
                .validate("expired-access-token");

        filter.doFilter(request, response, filterChain);

        verify(jwtUtil).validate("expired-access-token");
        verify(filterChain, never()).doFilter(request, response);
    }
}
