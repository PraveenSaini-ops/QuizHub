package com.quizhub.repository;

import com.quizhub.model.Difficulty;
import com.quizhub.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTopicId(Long topicId);

    List<Question> findByTopicIdAndDifficulty(Long topicId, Difficulty difficulty);

    List<Question> findByTextContainingIgnoreCase(String keyword);

    long countByTopicId(Long topicId);

    long countByTopicIdAndDifficulty(Long topicId, Difficulty difficulty);

    @Query("SELECT q FROM Question q WHERE " +
           "(:topicId IS NULL OR q.topic.id = :topicId) AND " +
           "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
           "(:search IS NULL OR LOWER(q.text) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Question> findFilteredQuestions(
            @Param("topicId") Long topicId,
            @Param("difficulty") Difficulty difficulty,
            @Param("search") String search,
            Pageable pageable
    );
}
