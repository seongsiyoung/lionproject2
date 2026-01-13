package com.example.lionproject2backend.qna.repository;

import com.example.lionproject2backend.qna.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    //특정 수업 질문 목록 조회
    List<Question> findByLessonId(Long questionId);

}
