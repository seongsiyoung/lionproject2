package com.example.lionproject2backend.tutorial.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.tutorial.dto.TutorialCreateRequest;
import com.example.lionproject2backend.tutorial.dto.TutorialResponse;
import com.example.lionproject2backend.tutorial.dto.TutorialStatusUpdateRequest;
import com.example.lionproject2backend.tutorial.dto.TutorialUpdateRequest;
import com.example.lionproject2backend.tutorial.service.TutorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tutorials")
@RequiredArgsConstructor
public class TutorialController {
    private final TutorialService tutorialService;

    @PostMapping
    public ResponseEntity<ApiResponse<TutorialResponse>> createTutorial(
            @RequestBody TutorialCreateRequest request
    ) {
        TutorialResponse response = tutorialService.createTutorial(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TutorialResponse>>> getAllTutorials() {
        List<TutorialResponse> tutorials = tutorialService.getAllTutorials();
        return ResponseEntity.ok(ApiResponse.success(tutorials));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TutorialResponse>> getTutorial(
            @PathVariable("id") Long tutorialId
    ) {
        TutorialResponse response = tutorialService.getTutorial(tutorialId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TutorialResponse>> updateTutorial(
            @PathVariable("id") Long tutorialId,
            @RequestBody TutorialUpdateRequest request
    ) {
        TutorialResponse response = tutorialService.updateTutorial(tutorialId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteTutorial(
            @PathVariable("id") Long tutorialId
    ) {
        Long deletedId = tutorialService.deleteTutorial(tutorialId);
        return ResponseEntity.ok(ApiResponse.success(deletedId));
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TutorialResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody TutorialStatusUpdateRequest request
    ) {
        TutorialResponse response = tutorialService.updateTutorialStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
