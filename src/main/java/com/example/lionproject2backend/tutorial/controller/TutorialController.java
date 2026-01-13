package com.example.lionproject2backend.tutorial.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.tutorial.dto.PostTutorialCreateRequest;
import com.example.lionproject2backend.tutorial.dto.GetTutorialResponse;
import com.example.lionproject2backend.tutorial.dto.PutTutorialStatusUpdateRequest;
import com.example.lionproject2backend.tutorial.dto.PutTutorialUpdateRequest;
import com.example.lionproject2backend.tutorial.service.TutorialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tutorials")
@RequiredArgsConstructor
public class TutorialController {
    private final TutorialService tutorialService;

    @PostMapping
    public ResponseEntity<ApiResponse<GetTutorialResponse>> createTutorial(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostTutorialCreateRequest request
    ) {
        GetTutorialResponse response = tutorialService.createTutorial(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetTutorialResponse>>> getAllTutorials() {
        List<GetTutorialResponse> tutorials = tutorialService.getAllTutorials();
        return ResponseEntity.ok(ApiResponse.success(tutorials));
    }


    @GetMapping("/{tutorialId}")
    public ResponseEntity<ApiResponse<GetTutorialResponse>> getTutorial(
            @PathVariable Long tutorialId
    ) {
        GetTutorialResponse response = tutorialService.getTutorial(tutorialId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{tutorialId}")
    public ResponseEntity<ApiResponse<GetTutorialResponse>> updateTutorial(
            @PathVariable Long tutorialId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PutTutorialUpdateRequest request
    ) {
        GetTutorialResponse response = tutorialService.updateTutorial(userId, tutorialId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{tutorialId}")
    public ResponseEntity<ApiResponse<Long>> deleteTutorial(
            @PathVariable Long tutorialId,
            @AuthenticationPrincipal Long userId
    ) {
        Long deletedId = tutorialService.deleteTutorial(userId, tutorialId);
        return ResponseEntity.ok(ApiResponse.success(deletedId));
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GetTutorialResponse>>> searchTutorials(
            @RequestParam String keyword
    ) {
        List<GetTutorialResponse> response =
                tutorialService.searchTutorials(keyword);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{tutorialId}/status")
    public ResponseEntity<ApiResponse<GetTutorialResponse>> updateStatus(
            @PathVariable Long tutorialId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PutTutorialStatusUpdateRequest request
    ) {
        GetTutorialResponse response = tutorialService.updateTutorialStatus(userId, tutorialId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
