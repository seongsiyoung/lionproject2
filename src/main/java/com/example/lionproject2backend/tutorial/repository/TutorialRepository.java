package com.example.lionproject2backend.tutorial.repository;

import com.example.lionproject2backend.tutorial.domain.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
}
