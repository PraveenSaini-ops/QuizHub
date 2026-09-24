package com.quizhub.service;

import com.quizhub.dto.QuizCreateDto;
import com.quizhub.model.Difficulty;
import com.quizhub.model.Quiz;
import com.quizhub.model.Topic;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.QuizRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.impl.QuizServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuizService Unit Tests")
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TopicService topicService;

    @InjectMocks
    private QuizServiceImpl quizService;

    private Topic sampleTopic;

    @BeforeEach
    void setUp() {
        sampleTopic = new Topic("Cloud Computing", "AWS & GCP", 18);
        sampleTopic.setId(1L);
    }

    @Test
    @DisplayName("createQuiz creates quiz with existing topicId")
    void testCreateQuizWithExistingTopic() {
        QuizCreateDto dto = new QuizCreateDto();
        dto.setTitle("AWS Solutions Architect Quiz");
        dto.setTopicId(1L);
        dto.setDurationMinutes(30);
        dto.setPassPercentage(70);
        dto.setSelectionMode("AUTO");
        dto.setAutoQuestionCount(5);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));
        when(questionRepository.findByTopicId(1L)).thenReturn(Collections.emptyList());
        when(quizRepository.existsByAccessCode(anyString())).thenReturn(false);
        when(quizRepository.save(any(Quiz.class))).thenAnswer(inv -> inv.getArgument(0));

        Quiz created = quizService.createQuiz(dto);

        assertNotNull(created);
        assertEquals("AWS Solutions Architect Quiz", created.getTitle());
        assertEquals(sampleTopic, created.getTopic());
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    @DisplayName("createQuiz creates quiz with newTopicName dynamically")
    void testCreateQuizWithNewTopicName() {
        Topic newTopic = new Topic("Kubernetes Mastery", "K8s architecture", null);
        newTopic.setId(50L);

        QuizCreateDto dto = new QuizCreateDto();
        dto.setTitle("K8s CKAD Assessment");
        dto.setNewTopicName("Kubernetes Mastery");
        dto.setDurationMinutes(20);
        dto.setPassPercentage(65);
        dto.setSelectionMode("AUTO");
        dto.setAutoQuestionCount(10);

        when(topicService.findOrCreateTopicByName("Kubernetes Mastery")).thenReturn(newTopic);
        when(questionRepository.findByTopicId(50L)).thenReturn(Collections.emptyList());
        when(quizRepository.existsByAccessCode(anyString())).thenReturn(false);
        when(quizRepository.save(any(Quiz.class))).thenAnswer(inv -> inv.getArgument(0));

        Quiz created = quizService.createQuiz(dto);

        assertNotNull(created);
        assertEquals("K8s CKAD Assessment", created.getTitle());
        assertEquals("Kubernetes Mastery", created.getTopic().getName());
        verify(topicService).findOrCreateTopicByName("Kubernetes Mastery");
        verify(quizRepository).save(any(Quiz.class));
    }
}
