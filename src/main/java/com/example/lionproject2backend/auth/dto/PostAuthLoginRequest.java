package com.example.lionproject2backend.auth.dto;

import lombok.Getter;

@Getter
public class PostAuthLoginRequest {
    private String email;
    private String password;
}
