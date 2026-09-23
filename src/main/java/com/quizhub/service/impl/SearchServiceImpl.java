package com.quizhub.service.impl;

import com.quizhub.dto.SearchResultDto;
import com.quizhub.model.Question;
import com.quizhub.model.Quiz;
import com.quizhub.model.Topic;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.QuizRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.SearchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public SearchServiceImpl(TopicRepository topicRepository,
                             QuestionRepository questionRepository,
                             QuizRepository quizRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public SearchResultDto globalSearch(String query) {
        SearchResultDto result = new SearchResultDto(query);
        if (query == null || query.trim().isEmpty()) {
            return result;
        }

        String q = query.trim();
        List<Topic> topics = topicRepository.findByNameContainingIgnoreCase(q);
        List<Question> questions = questionRepository.findByTextContainingIgnoreCase(q);
        List<Quiz> quizzes = quizRepository.findByTitleContainingIgnoreCase(q);

        result.setTopics(topics);
        result.setQuestions(questions);
        result.setQuizzes(quizzes);

        return result;
    }
}
