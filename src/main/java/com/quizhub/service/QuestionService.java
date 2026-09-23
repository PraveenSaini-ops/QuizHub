package com.quizhub.service;

import com.quizhub.dto.QuestionFormDto;
import com.quizhub.model.Difficulty;
import com.quizhub.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    Page<Question> findFilteredQuestions(Long topicId, Difficulty difficulty, String search, Pageable pageable);
    List<Question> findByTopicId(Long topicId);
    List<Question> findByTopicIdAndDifficulty(Long topicId, Difficulty difficulty);
    List<Question> searchByKeyword(String keyword);
    Optional<Question> findById(Long id);
    Question save(Question question);
    Question createQuestionFromDto(QuestionFormDto dto);
    Question updateQuestionFromDto(Long id, QuestionFormDto dto);
    void deleteById(Long id);
    QuestionFormDto toFormDto(Question question);
    long count();
}
