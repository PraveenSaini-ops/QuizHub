package com.quizhub.service;

import com.quizhub.dto.QuestionFormDto;
import com.quizhub.model.*;
import com.quizhub.repository.OptionRepository;
import com.quizhub.repository.QuestionRepository;
import com.quizhub.repository.TopicRepository;
import com.quizhub.service.impl.QuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuestionService Unit Tests")
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private TopicService topicService;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Topic sampleTopic;

    @BeforeEach
    void setUp() {
        sampleTopic = new Topic("Computer Science", "CS subjects", 18);
        sampleTopic.setId(1L);
    }

    @Test
    @DisplayName("createQuestionFromDto creates question with existing topicId")
    void testCreateQuestionWithTopicId() {
        QuestionFormDto dto = new QuestionFormDto();
        dto.setText("What is JVM?");
        dto.setTopicId(1L);
        dto.setDifficulty(Difficulty.EASY);
        dto.setQuestionType(QuestionType.MCQ);
        dto.setOptions(List.of(
                new QuestionFormDto.OptionDto("Java Virtual Machine", true),
                new QuestionFormDto.OptionDto("Java Variable Method", false)
        ));

        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        Question created = questionService.createQuestionFromDto(dto);

        assertNotNull(created);
        assertEquals("What is JVM?", created.getText());
        assertEquals(sampleTopic, created.getTopic());
        assertEquals(2, created.getOptions().size());
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("createQuestionFromDto creates question with newTopicName dynamically")
    void testCreateQuestionWithNewTopicName() {
        Topic newTopic = new Topic("Docker & DevOps", "Containers", null);
        newTopic.setId(20L);

        QuestionFormDto dto = new QuestionFormDto();
        dto.setText("What is Docker Compose?");
        dto.setNewTopicName("Docker & DevOps");
        dto.setDifficulty(Difficulty.MEDIUM);
        dto.setQuestionType(QuestionType.MCQ);
        dto.setOptions(List.of(
                new QuestionFormDto.OptionDto("Tool for defining multi-container apps", true),
                new QuestionFormDto.OptionDto("An IDE plugin", false)
        ));

        when(topicService.findOrCreateTopicByName("Docker & DevOps")).thenReturn(newTopic);
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        Question created = questionService.createQuestionFromDto(dto);

        assertNotNull(created);
        assertEquals("What is Docker Compose?", created.getText());
        assertEquals("Docker & DevOps", created.getTopic().getName());
        verify(topicService).findOrCreateTopicByName("Docker & DevOps");
        verify(questionRepository).save(any(Question.class));
    }
}
