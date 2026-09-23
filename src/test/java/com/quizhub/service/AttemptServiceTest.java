package com.quizhub.service;

import com.quizhub.dto.AttemptAnswerDto;
import com.quizhub.dto.QuizSubmissionDto;
import com.quizhub.exception.QuizNotFoundException;
import com.quizhub.exception.QuizTimeExpiredException;
import com.quizhub.model.*;
import com.quizhub.repository.*;
import com.quizhub.service.impl.AttemptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttemptService Unit Tests with Mockito")
class AttemptServiceTest {

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private AttemptAnswerRepository attemptAnswerRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AttemptServiceImpl attemptService;

    private User studentUser;
    private Quiz sampleQuiz;
    private Topic sampleTopic;
    private McqQuestion q1;

    @BeforeEach
    void setUp() {
        studentUser = new User("Alex Mercer", "student@quizhub.com", "pass", Role.ROLE_STUDENT);
        studentUser.setId(10L);

        sampleTopic = new Topic("Technology", "Tech & CS", 18);
        sampleTopic.setId(1L);

        sampleQuiz = new Quiz("Tech Quiz", "A test", sampleTopic, 15, QuizStatus.ACTIVE);
        sampleQuiz.setId(100L);
        sampleQuiz.setPassPercentage(60);

        q1 = new McqQuestion("What is HTTP?", "Protocol", Difficulty.EASY, sampleTopic);
        q1.setId(501L);
        Option opt1 = new Option(q1, "Protocol", true);
        opt1.setId(1001L);
        q1.addOption(opt1);

        sampleQuiz.setQuestions(Collections.singletonList(q1));
    }

    @Test
    @DisplayName("startAttempt: successfully initializes a new attempt with startedAt timestamp")
    void testStartAttempt() {
        when(quizRepository.findById(100L)).thenReturn(Optional.of(sampleQuiz));
        when(attemptRepository.findFirstByQuizIdAndUserIdAndSubmittedAtIsNullOrderByStartedAtDesc(100L, 10L))
                .thenReturn(Optional.empty());

        when(attemptRepository.save(any(Attempt.class))).thenAnswer(invocation -> {
            Attempt a = invocation.getArgument(0);
            a.setId(999L);
            return a;
        });

        Attempt result = attemptService.startAttempt(100L, studentUser);

        assertNotNull(result);
        assertEquals(999L, result.getId());
        assertEquals(studentUser, result.getUser());
        assertEquals(sampleQuiz, result.getQuiz());
        assertNotNull(result.getStartedAt());
    }

    @Test
    @DisplayName("startAttempt: throws QuizNotFoundException when quiz ID does not exist")
    void testStartAttemptQuizNotFound() {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> attemptService.startAttempt(999L, studentUser));
    }

    @Test
    @DisplayName("submitAttempt: computes score polymorphically and records passed status")
    void testSubmitAttemptScoring() {
        Attempt attempt = new Attempt(studentUser, sampleQuiz, 1);
        attempt.setId(123L);
        attempt.setStartedAt(Instant.now().minus(5, ChronoUnit.MINUTES)); // Started 5 mins ago (duration is 15 mins)

        when(attemptRepository.findById(123L)).thenReturn(Optional.of(attempt));
        when(attemptAnswerRepository.findByAttemptId(123L)).thenReturn(Collections.emptyList());
        when(attemptAnswerRepository.findByAttemptIdAndQuestionId(123L, 501L)).thenReturn(Optional.empty());
        when(attemptRepository.save(any(Attempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuizSubmissionDto submissionDto = new QuizSubmissionDto();
        submissionDto.setAttemptId(123L);
        submissionDto.setAnswers(Map.of(501L, List.of(1001L))); // Correct answer selected

        Attempt submitted = attemptService.submitAttempt(123L, submissionDto);

        assertNotNull(submitted.getSubmittedAt());
        assertEquals(1, submitted.getCorrectAnswers());
        assertEquals(100.0, submitted.getScorePercentage());
        assertTrue(submitted.isPassed());
    }

    @Test
    @DisplayName("submitAttempt: rejects submission when server-side time expired (> grace period)")
    void testSubmitAttemptExpiredTime() {
        Attempt expiredAttempt = new Attempt(studentUser, sampleQuiz, 1);
        expiredAttempt.setId(456L);
        // Started 30 mins ago for a 15-minute quiz (severely expired)
        expiredAttempt.setStartedAt(Instant.now().minus(30, ChronoUnit.MINUTES));

        when(attemptRepository.findById(456L)).thenReturn(Optional.of(expiredAttempt));

        QuizSubmissionDto submissionDto = new QuizSubmissionDto();
        submissionDto.setAttemptId(456L);

        assertThrows(QuizTimeExpiredException.class, () -> attemptService.submitAttempt(456L, submissionDto));
    }
}
