package com.quizhub.repository;

import com.quizhub.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    List<Attempt> findByUserIdOrderByStartedAtDesc(Long userId);

    List<Attempt> findByQuizIdOrderByStartedAtDesc(Long quizId);

    List<Attempt> findAllByOrderByStartedAtDesc();

    Optional<Attempt> findFirstByQuizIdAndUserIdAndSubmittedAtIsNullOrderByStartedAtDesc(Long quizId, Long userId);

    long countByPassedTrue();

    @Query("SELECT AVG(a.scorePercentage) FROM Attempt a WHERE a.submittedAt IS NOT NULL")
    Double calculateAverageScore();

    @Query("SELECT AVG(a.scorePercentage) FROM Attempt a WHERE a.quiz.topic.id = :topicId AND a.submittedAt IS NOT NULL")
    Double calculateAverageScoreByTopic(Long topicId);
}
