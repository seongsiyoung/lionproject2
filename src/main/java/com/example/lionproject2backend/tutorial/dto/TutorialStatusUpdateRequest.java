package com.example.lionproject2backend.tutorial.dto;

import com.example.lionproject2backend.tutorial.domain.TutorialStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TutorialStatusUpdateRequest {
    private TutorialStatus tutorialStatus;
}