package com.example.lionproject2backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 응답 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PutUserUpdateResponse {

    private Long id;
    private String nickname;
    private String introduction;
}
