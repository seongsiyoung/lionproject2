package com.example.lionproject2backend.tutorial.service;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.skill.domain.Skill;
import com.example.lionproject2backend.skill.repository.SkillRepository;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.dto.PostTutorialCreateRequest;
import com.example.lionproject2backend.tutorial.dto.GetTutorialResponse;
import com.example.lionproject2backend.tutorial.dto.PutTutorialStatusUpdateRequest;
import com.example.lionproject2backend.tutorial.dto.PutTutorialUpdateRequest;
import com.example.lionproject2backend.tutorial.repository.TutorialRepository;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
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

    public GetTutorialResponse createTutorial(Long userId, PostTutorialCreateRequest request) {

        Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTOR_FORBIDDEN));

        Tutorial tutorial = Tutorial.create(
                mentor,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getDuration()
        );

        // 스킬 등록 (없으면 생성/있으면 재사용) - MentorService와 동일한 방식
        if (request.getSkills() != null) {
            for (String skillName : request.getSkills()) {
                Skill skill = skillRepository.findBySkillName(skillName)
                        .orElseGet(() -> skillRepository.save(new Skill(skillName)));
                tutorial.addSkill(skill);
            }
        }

        Tutorial savedTutorial = tutorialRepository.save(tutorial);
        return GetTutorialResponse.from(savedTutorial);
    }


    @Transactional(readOnly = true)
    public GetTutorialResponse getTutorial(Long tutorialId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new CustomException(ErrorCode.TUTORIAL_NOT_FOUND));

        return GetTutorialResponse.from(tutorial);
    }

    @Transactional(readOnly = true)
    public List<GetTutorialResponse> getAllTutorials() {
        List<Tutorial> tutorials = tutorialRepository.findAll();
        return tutorials.stream()
                .map(GetTutorialResponse::from) // 단건 조회 방식과 동일하게 변환
                .toList();
    }


    public GetTutorialResponse updateTutorial(Long userId, Long tutorialId, PutTutorialUpdateRequest request) {

        Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTOR_FORBIDDEN));

        Tutorial tutorial = tutorialRepository.findByIdAndMentorId(tutorialId, mentor.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.TUTORIAL_FORBIDDEN));


        // 수정
        tutorial.update(
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getDuration()
        );

        List<Long> skillIds = request.getSkillIds(); // request에서 받은 ID 리스트

        if (skillIds != null && !skillIds.isEmpty()) {
                List<Skill> skills = skillRepository.findAllById(skillIds); // 실제 Skill 엔티티 가져오기
            if (skills.size() != skillIds.size()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            tutorial.updateSkills(skills); // 새 스킬로 교체
        } else {
            tutorial.clearSkills(); // 빈 리스트로 초기화
        }

        return GetTutorialResponse.from(tutorial);
    }


    public Long deleteTutorial(Long userId, Long tutorialId) {

        Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTOR_FORBIDDEN));

        Tutorial tutorial = tutorialRepository.findByIdAndMentorId(tutorialId, mentor.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.TUTORIAL_FORBIDDEN));


        Long id = tutorial.getId(); // 삭제 전에 잡아둠
        tutorialRepository.delete(tutorial);
        return id;
    }

    @Transactional(readOnly = true)
    public List<GetTutorialResponse> searchTutorials(String keyword) {

        List<Tutorial> tutorials =
                tutorialRepository
                        .findByTitleContainingOrDescriptionContaining(keyword, keyword);

        return tutorials.stream()
                .map(GetTutorialResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetTutorialResponse> getMyTutorials(Long userId) {
        Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTOR_FORBIDDEN));

        List<Tutorial> tutorials = tutorialRepository.findByMentorId(mentor.getId());
        return tutorials.stream()
                .map(GetTutorialResponse::from)
                .toList();
    }

    // 상태 업데이트
    public GetTutorialResponse updateTutorialStatus(Long userId, Long tutorialId, PutTutorialStatusUpdateRequest request) {

        Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTOR_FORBIDDEN));

        Tutorial tutorial = tutorialRepository.findByIdAndMentorId(tutorialId, mentor.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.TUTORIAL_FORBIDDEN));

        tutorial.changeStatus(request.getTutorialStatus());
        return GetTutorialResponse.from(tutorial);
    }
}


