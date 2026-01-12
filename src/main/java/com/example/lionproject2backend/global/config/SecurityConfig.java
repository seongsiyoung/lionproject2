package com.example.lionproject2backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 비활성화 (테스트 및 API 서버용)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. H2 콘솔이 iframe을 사용할 수 있도록 설정 (중요!)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            
            // 3. 모든 요청 허용 (현재 설정 유지)
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );

        return http.build();
    }
}