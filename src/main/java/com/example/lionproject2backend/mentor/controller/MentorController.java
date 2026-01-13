package com.example.lionproject2backend.mentor.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.mentor.dto.GetMentorDetailResponse;
import com.example.lionproject2backend.mentor.dto.PostMentorApplyRequest;
import com.example.lionproject2backend.mentor.dto.PostMentorApplyResponse;
import com.example.lionproject2backend.mentor.dto.GetMentorListResponse;
import com.example.lionproject2backend.mentor.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorController {
    private final MentorService mentorService;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<PostMentorApplyResponse>> applyMentor(
            @AuthenticationPrincipal Long userId, @RequestBody @Valid PostMentorApplyRequest request){

        PostMentorApplyResponse response = mentorService.postMentor(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetMentorListResponse>>> getMentors() {
        List<GetMentorListResponse> response = mentorService.getMentors();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{mentorId}")
    public ResponseEntity<ApiResponse<GetMentorDetailResponse>> getMentor(@PathVariable Long mentorId){
        GetMentorDetailResponse response = mentorService.getMentor(mentorId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
