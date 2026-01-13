package com.example.lionproject2backend.qna.repository;

import com.example.lionproject2backend.qna.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    //특정 질문 답변 목록 조회
    List<Answer> findByQuestionId(Long questionId);
}
