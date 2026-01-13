package com.example.lionproject2backend.user.dto;

import com.example.lionproject2backend.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 조회 응답 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDetailResponse {

    private Long id;
    private String email;
    private String nickname;
    private UserRole role;
    private String introduction;
}
