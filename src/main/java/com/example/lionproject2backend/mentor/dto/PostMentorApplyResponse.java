package com.example.lionproject2backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostMentorApplyResponse {

    private Long mentorId;
    private String status;

}
