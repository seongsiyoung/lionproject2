package com.example.lionproject2backend.qna.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.lesson.domain.Lesson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 하나의 수업에 여러개의 질문 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "code_content", columnDefinition = "TEXT")
    private String codeContent;

    /**
     * 질문 생성
     */
    public static Question create(Lesson lesson, String title, String content, String codeContent) {
        Question question = new Question();
        question.lesson = lesson;
        question.title = title;
        question.content = content;
        question.codeContent = codeContent;
        return question;
    }

}
