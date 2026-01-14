package com.example.lionproject2backend.tutorial.repository;

import com.example.lionproject2backend.tutorial.domain.Tutorial;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    @EntityGraph(attributePaths = {"mentor"})
    Optional<Tutorial> findWithMentorById(Long tutorialId);
    List<Tutorial> findByTitleContainingOrDescriptionContaining(String keyword, String keyword1);
    Optional<Tutorial> findByIdAndMentorId(Long tutorialId, Long mentorId);
    List<Tutorial> findByMentorId(Long mentorId);
}
