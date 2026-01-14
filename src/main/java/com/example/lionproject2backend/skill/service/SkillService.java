package com.example.lionproject2backend.skill.service;

import com.example.lionproject2backend.skill.dto.GetSkillResponse;
import com.example.lionproject2backend.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;

    public List<GetSkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(GetSkillResponse::from)
                .collect(Collectors.toList());
    }
}
