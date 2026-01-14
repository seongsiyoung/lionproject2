package com.example.lionproject2backend.skill.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.skill.dto.GetSkillResponse;
import com.example.lionproject2backend.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetSkillResponse>>> getAllSkills() {
        List<GetSkillResponse> skills = skillService.getAllSkills();
        return ResponseEntity.ok(ApiResponse.success(skills));
    }
}
