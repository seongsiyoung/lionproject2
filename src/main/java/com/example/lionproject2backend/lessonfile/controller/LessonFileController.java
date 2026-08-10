package com.example.lionproject2backend.lessonfile.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileDownloadUrlResponse;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileListResponse;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFileConfirmResponse;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFilePresignedPostRequest;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFilePresignedPostResponse;
import com.example.lionproject2backend.lessonfile.service.LessonFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonFileController {

    private final LessonFileService lessonFileService;

    @PostMapping("/lessons/{lessonId}/files/presigned-post")
    public ResponseEntity<ApiResponse<PostLessonFilePresignedPostResponse>> createPresignedPost(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostLessonFilePresignedPostRequest request
    ) {
        PostLessonFilePresignedPostResponse response = lessonFileService.createPresignedPost(
                lessonId,
                userId,
                request
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/lesson-files/{fileId}/confirm-upload")
    public ResponseEntity<ApiResponse<PostLessonFileConfirmResponse>> confirmUpload(
            @PathVariable Long fileId,
            @AuthenticationPrincipal Long userId
    ) {
        PostLessonFileConfirmResponse response = lessonFileService.confirmUpload(fileId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lessons/{lessonId}/files")
    public ResponseEntity<ApiResponse<GetLessonFileListResponse>> getLessonFiles(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonFileListResponse response = lessonFileService.getLessonFiles(lessonId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lesson-files/{fileId}/download-url")
    public ResponseEntity<ApiResponse<GetLessonFileDownloadUrlResponse>> createDownloadUrl(
            @PathVariable Long fileId,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonFileDownloadUrlResponse response = lessonFileService.createDownloadUrl(fileId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/lesson-files/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal Long userId
    ) {
        lessonFileService.deleteFile(fileId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
