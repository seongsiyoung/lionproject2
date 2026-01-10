package com.example.lionproject2backend.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostAuthSignupResponse {

    private Long id;

    private String email;

    private String nickname;

    private String role;
}

