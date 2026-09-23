package com.quizhub.service;

import com.quizhub.dto.TopicCardDto;
import com.quizhub.model.Topic;
import java.util.List;
import java.util.Optional;

public interface TopicService {
    List<Topic> findAll();
    List<TopicCardDto> findAllTopicCards();
    Optional<Topic> findById(Long id);
    Topic save(Topic topic);
    Topic createTopic(String name, String description, Integer openTdbCategoryId);
    Topic updateTopic(Long id, String name, String description, Integer openTdbCategoryId);
    void deleteById(Long id);
    TopicCardDto getTopicCard(Long topicId);
}
