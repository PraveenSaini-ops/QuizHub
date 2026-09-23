package com.quizhub.service.impl;

import com.quizhub.dto.TopicCardDto;
import com.quizhub.exception.ResourceNotFoundException;
import com.quizhub.model.Difficulty;
import com.quizhub.model.QuizStatus;
import com.quizhub.model.Topic;
import com.quizhub.repository.AttemptRepository;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.QuizRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final AttemptRepository attemptRepository;

    public TopicServiceImpl(TopicRepository topicRepository,
                            QuestionRepository questionRepository,
                            QuizRepository quizRepository,
                            AttemptRepository attemptRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicCardDto> findAllTopicCards() {
        List<Topic> topics = topicRepository.findAll();
        List<TopicCardDto> cards = new ArrayList<>();
        for (Topic topic : topics) {
            cards.add(buildTopicCard(topic));
        }
        return cards;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    @Override
    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    @Override
    public Topic createTopic(String name, String description, Integer openTdbCategoryId) {
        Topic topic = new Topic(name, description, openTdbCategoryId);
        return topicRepository.save(topic);
    }

    @Override
    public Topic updateTopic(Long id, String name, String description, Integer openTdbCategoryId) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        topic.setName(name);
        topic.setDescription(description);
        topic.setOpenTdbCategoryId(openTdbCategoryId);
        return topicRepository.save(topic);
    }

    @Override
    public void deleteById(Long id) {
        topicRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicCardDto getTopicCard(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));
        return buildTopicCard(topic);
    }

    private TopicCardDto buildTopicCard(Topic topic) {
        long totalQ = questionRepository.countByTopicId(topic.getId());
        long easyQ = questionRepository.countByTopicIdAndDifficulty(topic.getId(), Difficulty.EASY);
        long medQ = questionRepository.countByTopicIdAndDifficulty(topic.getId(), Difficulty.MEDIUM);
        long hardQ = questionRepository.countByTopicIdAndDifficulty(topic.getId(), Difficulty.HARD);

        long activeQuizzes = quizRepository.findByTopicId(topic.getId()).stream()
                .filter(q -> q.getStatus() == QuizStatus.ACTIVE)
                .count();

        Double avgScore = attemptRepository.calculateAverageScoreByTopic(topic.getId());
        double avgScoreVal = avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 75.0;

        int completion = totalQ > 0 ? (int) Math.min(100, Math.round((activeQuizzes * 15.0 + totalQ * 2.0))) : 0;
        if (completion > 100) completion = 85;

        return new TopicCardDto(topic, totalQ, easyQ, medQ, hardQ, activeQuizzes, avgScoreVal, completion);
    }
}
