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
    private final com.quizhub.service.TopicService topicService;

    public QuizServiceImpl(QuizRepository quizRepository,
                           TopicRepository topicRepository,
                           QuestionRepository questionRepository,
                           com.quizhub.service.TopicService topicService) {
        this.quizRepository = quizRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.topicService = topicService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Quiz> findFilteredQuizzes(Long topicId, QuizStatus status, String search, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Quiz> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (topicId != null) {
                predicates.add(cb.equal(root.get("topic").get("id"), topicId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return quizRepository.findAll(spec, pageable);
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
    @Transactional(readOnly = true)
    public Optional<Quiz> findByAccessCode(String accessCode) {
        if (accessCode == null || accessCode.trim().isEmpty()) {
            return Optional.empty();
        }
        return quizRepository.findByAccessCodeIgnoreCase(accessCode.trim());
    }

    @Override
    public Quiz createQuiz(QuizCreateDto dto) {
        Topic topic = resolveTopic(dto.getTopicId(), dto.getNewTopicName());

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTopic(topic);
        quiz.setDurationMinutes(dto.getDurationMinutes());
        quiz.setPassPercentage(dto.getPassPercentage());
        quiz.setStatus(dto.getStatus() != null ? dto.getStatus() : QuizStatus.ACTIVE);

        // Handle Access Code
        String code = dto.getAccessCode();
        if (code == null || code.trim().isEmpty()) {
            String prefix = topic.getName().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
            if (prefix.length() > 4) prefix = prefix.substring(0, 4);
            code = generateUniqueAccessCode(prefix.isEmpty() ? "QUIZ" : prefix);
        } else {
            code = code.trim().toUpperCase();
        }
        quiz.setAccessCode(code);

        // Handle Access Password
        String pass = dto.getAccessPassword();
        if (pass != null && !pass.trim().isEmpty()) {
            quiz.setAccessPassword(pass.trim());
        } else {
            quiz.setAccessPassword(null);
        }

        List<Question> questions = resolveQuestions(dto, topic);
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuiz(Long id, QuizCreateDto dto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        Topic topic = resolveTopic(dto.getTopicId(), dto.getNewTopicName());

        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTopic(topic);
        quiz.setDurationMinutes(dto.getDurationMinutes());
        quiz.setPassPercentage(dto.getPassPercentage());
        if (dto.getStatus() != null) {
            quiz.setStatus(dto.getStatus());
        }

        if (dto.getAccessCode() != null && !dto.getAccessCode().trim().isEmpty()) {
            quiz.setAccessCode(dto.getAccessCode().trim().toUpperCase());
        }
        if (dto.getAccessPassword() != null) {
            quiz.setAccessPassword(dto.getAccessPassword().trim().isEmpty() ? null : dto.getAccessPassword().trim());
        }

        List<Question> questions = resolveQuestions(dto, topic);
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    private Topic resolveTopic(Long topicId, String newTopicName) {
        if (newTopicName != null && !newTopicName.trim().isEmpty()) {
            return topicService.findOrCreateTopicByName(newTopicName.trim());
        }
        if (topicId != null) {
            return topicRepository.findById(topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));
        }
        throw new ResourceNotFoundException("Topic is required. Please select or write a new topic.");
    }

    private String generateUniqueAccessCode(String prefix) {
        String code;
        int attempts = 0;
        do {
            int randomNum = 1000 + (int)(Math.random() * 9000);
            code = prefix + "-" + randomNum;
            attempts++;
            if (attempts > 50) {
                code = prefix + "-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                break;
            }
        } while (quizRepository.existsByAccessCode(code));
        return code;
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
