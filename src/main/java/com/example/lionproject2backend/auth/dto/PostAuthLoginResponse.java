package com.example.lionproject2backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAuthLoginResponse {
    private String accessToken;
}
