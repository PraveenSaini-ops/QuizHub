package com.quizhub.repository;

import com.quizhub.model.Quiz;
import com.quizhub.model.QuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {

    List<Quiz> findByTopicId(Long topicId);

    List<Quiz> findByStatus(QuizStatus status);

    List<Quiz> findByTitleContainingIgnoreCase(String keyword);

    Optional<Quiz> findByAccessCodeIgnoreCase(String accessCode);

    boolean existsByAccessCode(String accessCode);

    long countByStatus(QuizStatus status);
}