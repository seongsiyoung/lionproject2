package com.example.lionproject2backend.mentor.service;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.domain.MentorSkill;
import com.example.lionproject2backend.mentor.domain.MentorStatus;
import com.example.lionproject2backend.mentor.dto.GetMentorDetailResponse;
import com.example.lionproject2backend.mentor.dto.PostMentorApplyRequest;
import com.example.lionproject2backend.mentor.dto.PostMentorApplyResponse;
import com.example.lionproject2backend.mentor.dto.GetMentorListResponse;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.mentor.repository.MentorSkillRepository;
import com.example.lionproject2backend.skill.domain.Skill;
import com.example.lionproject2backend.skill.repository.SkillRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {

    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final MentorSkillRepository mentorSkillRepository;

    /**
     * 멘토 신청 (자동 승인 상태)
     * 스킬 정보도 함께 저장
     */
    @Transactional
    public PostMentorApplyResponse postMentor(Long userId, PostMentorApplyRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (mentorRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 멘토로 등록되어 있습니다.");
        }

        // 멘토 생성 (APPROVED)
        Mentor mentor = new Mentor(user, request.getCareer());
        Mentor savedMentor = mentorRepository.save(mentor);

        // 스킬 등록 (없으면 생성/있으면 재사용)
        for (String skillName : request.getSkills()) {
            Skill skill = skillRepository.findBySkillName(skillName)
                    .orElseGet(() -> skillRepository.save(new Skill(skillName)));

            MentorSkill mentorSkill = new MentorSkill(savedMentor, skill);
            mentorSkillRepository.save(mentorSkill);
        }

        return new PostMentorApplyResponse(savedMentor.getId(), "APPROVED");

    }

    /**
     * 멘토 목록 조회
     */

    public List<GetMentorListResponse> getMentors() {
        List<Mentor> mentors = mentorRepository.findByMentorStatus(MentorStatus.APPROVED);

        return mentors.stream()
                .map(mentor -> {
                    // 멘토별 스킬 목록 조회
                    List<String> skills = mentorSkillRepository.findByMentorId(mentor.getId())
                            .stream()
                            .map(ms -> ms.getSkill().getSkillName())
                            .collect(Collectors.toList());

                    return new GetMentorListResponse(
                            mentor.getId(),
                            mentor.getUser().getNickname(),
                            mentor.getCareer(),
                            mentor.getReviewCount(),
                            skills
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 멘토 상세 조회
     */

    public GetMentorDetailResponse getMentor(Long mentorId) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("멘토를 찾을 수 없습니다."));

        List<String> skills = mentorSkillRepository.findByMentorId(mentorId)
                .stream()
                .map(ms -> ms.getSkill().getSkillName())
                .collect(Collectors.toList());

        return new GetMentorDetailResponse(
                mentor.getId(),
                mentor.getUser().getNickname(),
                mentor.getCareer(),
                mentor.getReviewCount(),
                skills
        );
    }
}
