package com.example.lionproject2backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostDuplicateCheckResponse {
    private boolean duplicated;
}
