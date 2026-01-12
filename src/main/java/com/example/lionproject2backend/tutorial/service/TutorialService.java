package com.example.lionproject2backend.tutorial.service;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.skill.domain.Skill;
import com.example.lionproject2backend.skill.repository.SkillRepository;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.domain.TutorialSkill;
import com.example.lionproject2backend.tutorial.dto.TutorialCreateRequest;
import com.example.lionproject2backend.tutorial.dto.TutorialResponse;
import com.example.lionproject2backend.tutorial.dto.TutorialStatusUpdateRequest;
import com.example.lionproject2backend.tutorial.dto.TutorialUpdateRequest;
import com.example.lionproject2backend.tutorial.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TutorialService {

    private final TutorialRepository tutorialRepository;
    private final MentorRepository mentorRepository;
    private final SkillRepository skillRepository;



    public TutorialResponse createTutorial(TutorialCreateRequest request) {

        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토입니다."));

        Tutorial tutorial = Tutorial.create(
                mentor,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getDuration()
        );

        // 스킬 조회 (DB에 있는 것만)
        List<Skill> skills = skillRepository.findAllById(request.getSkillIds());

        // 검증: 요청한 개수 != 실제 조회 개수
        if (skills.size() != request.getSkillIds().size()) {
            throw new IllegalArgumentException("존재하지 않는 스킬이 포함되어 있습니다.");
        }

        // 튜토리얼에 스킬 연결
        for (Skill skill : skills) {
            tutorial.addSkill(skill);
        }

        Tutorial savedTutorial = tutorialRepository.save(tutorial);
        return TutorialResponse.from(savedTutorial);
    }


    @Transactional(readOnly = true)
    public TutorialResponse getTutorial(Long tutorialId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new IllegalArgumentException("튜토리얼을 찾을 수 없습니다."));

        return TutorialResponse.from(tutorial);
    }

    @Transactional(readOnly = true)
    public List<TutorialResponse> getAllTutorials() {
        List<Tutorial> tutorials = tutorialRepository.findAll();
        return tutorials.stream()
                .map(TutorialResponse::from) // 단건 조회 방식과 동일하게 변환
                .toList();
    }


    public TutorialResponse updateTutorial(Long tutorialId, TutorialUpdateRequest request) {

            Tutorial tutorial = tutorialRepository.findById(tutorialId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 튜토리얼"));


            tutorial.setTitle(request.getTitle());
            tutorial.setDescription(request.getDescription());
            tutorial.setPrice(request.getPrice());
            tutorial.setDuration(request.getDuration());

            List<Long> skillIds = request.getSkillIds(); // request에서 받은 ID 리스트

            if (skillIds != null && !skillIds.isEmpty()) {
                List<Skill> skills = skillRepository.findAllById(skillIds); // 실제 Skill 엔티티 가져오기
                if (skills.size() != skillIds.size()) {
                    throw new IllegalArgumentException("존재하지 않는 스킬이 포함되어 있습니다.");
                }
                tutorial.updateSkills(skills); // 새 스킬로 교체
            } else {
                tutorial.clearSkills(); // 빈 리스트로 초기화
            }

            return TutorialResponse.from(tutorial);
    }


    public Long deleteTutorial(Long tutorialId) {

        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 튜토리얼"));

        Long id = tutorial.getId(); // 삭제 전에 잡아둠
        tutorialRepository.delete(tutorial);
        return id;
    }

    // 상태 업데이트
    public TutorialResponse updateTutorialStatus(Long tutorialId, TutorialStatusUpdateRequest request) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 튜토리얼"));

        tutorial.setTutorialStatus(request.getTutorialStatus());

        return TutorialResponse.from(tutorial);
    }
}


