package com.quizhub.repository;

import com.quizhub.model.Difficulty;
import com.quizhub.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    List<Question> findByTopicId(Long topicId);

    List<Question> findByTopicIdAndDifficulty(Long topicId, Difficulty difficulty);

    List<Question> findByTextContainingIgnoreCase(String keyword);

    long countByTopicId(Long topicId);

    long countByTopicIdAndDifficulty(Long topicId, Difficulty difficulty);
}