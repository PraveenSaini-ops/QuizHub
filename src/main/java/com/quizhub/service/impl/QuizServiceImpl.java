package com.quizhub.service.impl;

import com.quizhub.dto.QuizCreateDto;
import com.quizhub.exception.ResourceNotFoundException;
import com.quizhub.model.Question;
import com.quizhub.model.Quiz;
import com.quizhub.model.QuizStatus;
import com.quizhub.model.Topic;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.QuizRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;

    public QuizServiceImpl(QuizRepository quizRepository,
                           TopicRepository topicRepository,
                           QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Quiz> findFilteredQuizzes(Long topicId, QuizStatus status, String search, Pageable pageable) {
        String searchParam = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return quizRepository.findFilteredQuizzes(topicId, status, searchParam, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> findByStatus(QuizStatus status) {
        return quizRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> findActiveQuizzes() {
        return quizRepository.findByStatus(QuizStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Quiz> findById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Quiz createQuiz(QuizCreateDto dto) {
        Topic topic = topicRepository.findById(dto.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + dto.getTopicId()));

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTopic(topic);
        quiz.setDurationMinutes(dto.getDurationMinutes());
        quiz.setPassPercentage(dto.getPassPercentage());
        quiz.setStatus(dto.getStatus() != null ? dto.getStatus() : QuizStatus.ACTIVE);

        List<Question> questions = resolveQuestions(dto, topic);
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuiz(Long id, QuizCreateDto dto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        Topic topic = topicRepository.findById(dto.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + dto.getTopicId()));

        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTopic(topic);
        quiz.setDurationMinutes(dto.getDurationMinutes());
        quiz.setPassPercentage(dto.getPassPercentage());
        if (dto.getStatus() != null) {
            quiz.setStatus(dto.getStatus());
        }

        List<Question> questions = resolveQuestions(dto, topic);
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    @Override
    public void deleteById(Long id) {
        quizRepository.deleteById(id);
    }

    @Override
    public Quiz updateStatus(Long id, QuizStatus status) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        quiz.setStatus(status);
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return quizRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(QuizStatus status) {
        return quizRepository.countByStatus(status);
    }

    private List<Question> resolveQuestions(QuizCreateDto dto, Topic topic) {
        List<Question> questions = new ArrayList<>();
        if ("MANUAL".equalsIgnoreCase(dto.getSelectionMode()) && dto.getSelectedQuestionIds() != null && !dto.getSelectedQuestionIds().isEmpty()) {
            questions = questionRepository.findAllById(dto.getSelectedQuestionIds());
        } else {
            // Auto selection mode with Collections.shuffle
            List<Question> pool;
            if (dto.getDifficultyFilter() != null) {
                pool = questionRepository.findByTopicIdAndDifficulty(topic.getId(), dto.getDifficultyFilter());
            } else {
                pool = questionRepository.findByTopicId(topic.getId());
            }

            if (pool.isEmpty()) {
                // If topic has no questions in specific difficulty, fallback to any topic questions
                pool = questionRepository.findByTopicId(topic.getId());
            }

            List<Question> shuffled = new ArrayList<>(pool);
            Collections.shuffle(shuffled);

            int targetCount = Math.min(dto.getAutoQuestionCount(), shuffled.size());
            if (targetCount > 0) {
                questions = new ArrayList<>(shuffled.subList(0, targetCount));
            }
        }
        return questions;
    }
}
