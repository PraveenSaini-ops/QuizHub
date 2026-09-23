package com.quizhub.repository;

import com.quizhub.model.Quiz;
import com.quizhub.model.QuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByTopicId(Long topicId);

    List<Quiz> findByStatus(QuizStatus status);

    List<Quiz> findByTitleContainingIgnoreCase(String keyword);

    java.util.Optional<Quiz> findByAccessCodeIgnoreCase(String accessCode);

    boolean existsByAccessCode(String accessCode);

    @Query("SELECT q FROM Quiz q WHERE " +
           "(:topicId IS NULL OR q.topic.id = :topicId) AND " +
           "(:status IS NULL OR q.status = :status) AND " +
           "(:search IS NULL OR LOWER(q.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Quiz> findFilteredQuizzes(
            @Param("topicId") Long topicId,
            @Param("status") QuizStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    long countByStatus(QuizStatus status);
}
