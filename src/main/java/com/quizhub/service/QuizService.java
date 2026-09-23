package com.quizhub.service;

import com.quizhub.dto.QuizCreateDto;
import com.quizhub.model.Quiz;
import com.quizhub.model.QuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QuizService {
    Page<Quiz> findFilteredQuizzes(Long topicId, QuizStatus status, String search, Pageable pageable);
    List<Quiz> findByStatus(QuizStatus status);
    List<Quiz> findActiveQuizzes();
    List<Quiz> findAll();
    Optional<Quiz> findById(Long id);
    Quiz createQuiz(QuizCreateDto dto);
    Quiz updateQuiz(Long id, QuizCreateDto dto);
    void deleteById(Long id);
    Quiz updateStatus(Long id, QuizStatus status);
    long count();
    long countByStatus(QuizStatus status);
}
