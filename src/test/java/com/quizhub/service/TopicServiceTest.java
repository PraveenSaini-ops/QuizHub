package com.quizhub.service;

import com.quizhub.model.Topic;
import com.quizhub.repository.AttemptRepository;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.QuizRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.impl.TopicServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TopicService Unit Tests")
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private AttemptRepository attemptRepository;

    @InjectMocks
    private TopicServiceImpl topicService;

    private Topic existingTopic;

    @BeforeEach
    void setUp() {
        existingTopic = new Topic("Mathematics", "Calculus and Algebra", 19);
        existingTopic.setId(5L);
    }

    @Test
    @DisplayName("findOrCreateTopicByName returns existing topic when found ignoring case")
    void testFindOrCreateTopicByNameExisting() {
        when(topicRepository.findByNameIgnoreCase("mathematics")).thenReturn(Optional.of(existingTopic));

        Topic result = topicService.findOrCreateTopicByName("mathematics");

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Mathematics", result.getName());
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    @DisplayName("findOrCreateTopicByName creates and returns new topic when not found")
    void testFindOrCreateTopicByNameNew() {
        when(topicRepository.findByNameIgnoreCase("Artificial Intelligence")).thenReturn(Optional.empty());
        when(topicRepository.save(any(Topic.class))).thenAnswer(inv -> {
            Topic t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        Topic result = topicService.findOrCreateTopicByName(" Artificial Intelligence ", "AI & ML");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Artificial Intelligence", result.getName());
        assertEquals("AI & ML", result.getDescription());
        verify(topicRepository).save(any(Topic.class));
    }

    @Test
    @DisplayName("findOrCreateTopicByName throws exception when name is blank")
    void testFindOrCreateTopicByNameBlank() {
        assertThrows(IllegalArgumentException.class, () -> topicService.findOrCreateTopicByName("   "));
    }
}
