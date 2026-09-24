package com.quizhub.service.impl;

import com.quizhub.dto.QuestionFormDto;
import com.quizhub.exception.InvalidAnswerException;
import com.quizhub.exception.ResourceNotFoundException;
import com.quizhub.model.*;
import com.quizhub.repository.OptionRepository;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final OptionRepository optionRepository;
    private final com.quizhub.service.TopicService topicService;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               TopicRepository topicRepository,
                               OptionRepository optionRepository,
                               com.quizhub.service.TopicService topicService) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
        this.optionRepository = optionRepository;
        this.topicService = topicService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Question> findFilteredQuestions(Long topicId, Difficulty difficulty, String search, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Question> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (topicId != null) {
                predicates.add(cb.equal(root.get("topic").get("id"), topicId));
            }
            if (difficulty != null) {
                predicates.add(cb.equal(root.get("difficulty"), difficulty));
            }
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("text")), pattern));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return questionRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByTopicId(Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByTopicIdAndDifficulty(Long topicId, Difficulty difficulty) {
        return questionRepository.findByTopicIdAndDifficulty(topicId, difficulty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> searchByKeyword(String keyword) {
        return questionRepository.findByTextContainingIgnoreCase(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public Question save(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question createQuestionFromDto(QuestionFormDto dto) {
        Topic topic = resolveTopic(dto.getTopicId(), dto.getNewTopicName());

        Question question = instantiateQuestionByType(dto.getQuestionType(), dto.getText(), dto.getExplanation(), dto.getDifficulty(), topic);
        populateOptions(question, dto);
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestionFromDto(Long id, QuestionFormDto dto) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        Topic topic = resolveTopic(dto.getTopicId(), dto.getNewTopicName());

        // If question type changed, replace with a newly typed instance
        if (existing.getQuestionType() != dto.getQuestionType()) {
            questionRepository.delete(existing);
            questionRepository.flush();
            Question newTyped = instantiateQuestionByType(dto.getQuestionType(), dto.getText(), dto.getExplanation(), dto.getDifficulty(), topic);
            populateOptions(newTyped, dto);
            return questionRepository.save(newTyped);
        }

        existing.setText(dto.getText());
        existing.setExplanation(dto.getExplanation());
        existing.setDifficulty(dto.getDifficulty());
        existing.setTopic(topic);

        existing.getOptions().clear();
        populateOptions(existing, dto);
        return questionRepository.save(existing);
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

    @Override
    public void deleteById(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionFormDto toFormDto(Question question) {
        QuestionFormDto dto = new QuestionFormDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setExplanation(question.getExplanation());
        dto.setDifficulty(question.getDifficulty());
        dto.setTopicId(question.getTopic().getId());
        dto.setQuestionType(question.getQuestionType());

        List<QuestionFormDto.OptionDto> optionDtos = new ArrayList<>();
        for (Option opt : question.getOptions()) {
            QuestionFormDto.OptionDto optDto = new QuestionFormDto.OptionDto();
            optDto.setId(opt.getId());
            optDto.setText(opt.getText());
            optDto.setCorrect(opt.isCorrect());
            optionDtos.add(optDto);
        }
        dto.setOptions(optionDtos);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return questionRepository.count();
    }

    private Question instantiateQuestionByType(QuestionType type, String text, String explanation, Difficulty difficulty, Topic topic) {
        return switch (type) {
            case MCQ -> new McqQuestion(text, explanation, difficulty, topic);
            case TRUE_FALSE -> new TrueFalseQuestion(text, explanation, difficulty, topic);
            case MULTI_SELECT -> new MultiSelectQuestion(text, explanation, difficulty, topic);
        };
    }

    private void populateOptions(Question question, QuestionFormDto dto) {
        List<QuestionFormDto.OptionDto> optDtos = dto.getOptions();
        if (optDtos == null || optDtos.isEmpty()) {
            throw new InvalidAnswerException("A question must have at least one option.");
        }

        boolean hasCorrect = false;
        for (QuestionFormDto.OptionDto optDto : optDtos) {
            if (optDto.getText() != null && !optDto.getText().trim().isEmpty()) {
                Option option = new Option(question, optDto.getText().trim(), optDto.isCorrect());
                question.addOption(option);
                if (optDto.isCorrect()) {
                    hasCorrect = true;
                }
            }
        }

        if (!hasCorrect) {
            throw new InvalidAnswerException("At least one option must be marked as correct.");
        }
    }
}
