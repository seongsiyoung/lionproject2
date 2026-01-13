package com.example.lionproject2backend.user.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.user.dto.GetUserDetailResponse;
import com.example.lionproject2backend.user.dto.PutUserUpdateRequest;
import com.example.lionproject2backend.user.dto.PutUserUpdateResponse;
import com.example.lionproject2backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


// jwt 구현 후 userId 수정!

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회
     * GET /api/user/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetUserDetailResponse>> getUser(
            @AuthenticationPrincipal Long userId) {
        GetUserDetailResponse response = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 정보 수정
     * PUT /api/user/me
     */

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<PutUserUpdateResponse>> updateUser(
            @AuthenticationPrincipal Long userId, @RequestBody PutUserUpdateRequest putUserUpdateRequest) {
    PutUserUpdateResponse response = userService.updateUser(userId, putUserUpdateRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
    }
}

