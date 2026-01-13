package com.example.lionproject2backend.mentor.repository.querydsl.custom;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.dto.MentorSearchCondition;

import java.util.List;

public interface MentorRepositoryCustom {
    List<Mentor> searchMentors(MentorSearchCondition condition);

}
