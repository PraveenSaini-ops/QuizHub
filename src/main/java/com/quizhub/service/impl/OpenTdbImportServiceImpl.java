package com.quizhub.service.impl;

import com.quizhub.dto.OpenTdbResponseDto;
import com.quizhub.exception.QuestionFetchException;
import com.quizhub.exception.ResourceNotFoundException;
import com.quizhub.model.*;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.OpenTdbImportService;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
@Transactional
public class OpenTdbImportServiceImpl implements OpenTdbImportService {

    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final RestTemplate restTemplate;

    public OpenTdbImportServiceImpl(TopicRepository topicRepository,
                                    QuestionRepository questionRepository,
                                    RestTemplateBuilder restTemplateBuilder) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public List<Question> importQuestions(Long topicId, int amount, Difficulty difficulty) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

        Integer categoryId = topic.getOpenTdbCategoryId();
        if (categoryId == null) {
            // Default mapping
            categoryId = resolveDefaultCategoryId(topic.getName());
        }

        int requestedAmount = Math.max(1, Math.min(50, amount));
        String diffParam = difficulty != null ? difficulty.name().toLowerCase() : "";

        StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?");
        urlBuilder.append("amount=").append(requestedAmount);
        if (categoryId != null && categoryId > 0) {
            urlBuilder.append("&category=").append(categoryId);
        }
        if (!diffParam.isEmpty()) {
            urlBuilder.append("&difficulty=").append(diffParam);
        }

        OpenTdbResponseDto response;
        try {
            response = restTemplate.getForObject(urlBuilder.toString(), OpenTdbResponseDto.class);
        } catch (Exception ex) {
            throw new QuestionFetchException("Failed to fetch questions from Open Trivia DB: " + ex.getMessage(), ex);
        }

        if (response == null || response.getResponseCode() != 0 || response.getResults() == null || response.getResults().isEmpty()) {
            throw new QuestionFetchException("Open Trivia DB returned error or no questions available (Response Code: " + 
                    (response != null ? response.getResponseCode() : "null") + ")");
        }

        List<Question> savedQuestions = new ArrayList<>();
        for (OpenTdbResponseDto.OpenTdbQuestionItem item : response.getResults()) {
            String questionText = StringEscapeUtils.unescapeHtml4(item.getQuestion());
            String correctAnswerText = StringEscapeUtils.unescapeHtml4(item.getCorrectAnswer());
            Difficulty itemDiff = parseDifficulty(item.getDifficulty(), difficulty);

            Question question;
            if ("boolean".equalsIgnoreCase(item.getType())) {
                question = new TrueFalseQuestion(questionText, "Imported from Open Trivia DB (Topic: " + topic.getName() + ")", itemDiff, topic);
                Option optTrue = new Option(question, "True", "True".equalsIgnoreCase(correctAnswerText));
                Option optFalse = new Option(question, "False", "False".equalsIgnoreCase(correctAnswerText));
                question.addOption(optTrue);
                question.addOption(optFalse);
            } else {
                question = new McqQuestion(questionText, "Imported from Open Trivia DB (Topic: " + topic.getName() + ")", itemDiff, topic);
                List<Option> options = new ArrayList<>();
                options.add(new Option(question, correctAnswerText, true));

                if (item.getIncorrectAnswers() != null) {
                    for (String inc : item.getIncorrectAnswers()) {
                        options.add(new Option(question, StringEscapeUtils.unescapeHtml4(inc), false));
                    }
                }
                Collections.shuffle(options);
                for (Option opt : options) {
                    question.addOption(opt);
                }
            }

            savedQuestions.add(questionRepository.save(question));
        }

        return savedQuestions;
    }

    private Integer resolveDefaultCategoryId(String topicName) {
        if (topicName == null) return null;
        String lower = topicName.toLowerCase();
        if (lower.contains("science")) return 17;
        if (lower.contains("history")) return 23;
        if (lower.contains("geography")) return 22;
        if (lower.contains("math")) return 19;
        if (lower.contains("tech") || lower.contains("computer")) return 18;
        return null;
    }

    private Difficulty parseDifficulty(String text, Difficulty fallback) {
        if (text == null) return fallback != null ? fallback : Difficulty.MEDIUM;
        return switch (text.toLowerCase()) {
            case "easy" -> Difficulty.EASY;
            case "hard" -> Difficulty.HARD;
            default -> Difficulty.MEDIUM;
        };
    }
}
