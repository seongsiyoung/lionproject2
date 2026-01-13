package com.example.lionproject2backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 요청 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PutUserUpdateRequest {

    private String nickname;
    private String introduction;
}
