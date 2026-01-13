package com.example.lionproject2backend.tutorial.dto;

import com.example.lionproject2backend.tutorial.domain.TutorialStatus;
import lombok.*;

@Getter
@NoArgsConstructor
public class PutTutorialStatusUpdateRequest {
    private TutorialStatus tutorialStatus;
}