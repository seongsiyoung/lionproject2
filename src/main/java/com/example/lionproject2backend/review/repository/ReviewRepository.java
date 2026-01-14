package com.example.lionproject2backend.review.repository;

import com.example.lionproject2backend.review.domain.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByTutorialIdAndMenteeId(Long tutorialId, Long menteeId);

    Optional<Review> findByTutorialIdAndMenteeId(Long tutorialId, Long menteeId);

    Page<Review> findByTutorialId(Long tutorialId, Pageable pageable);

    Optional<Review> findByIdAndTutorialId(Long reviewId, Long tutorialId);

    List<Review> findByMentorId(Long mentorId);
}
