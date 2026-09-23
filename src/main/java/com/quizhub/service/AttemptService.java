package com.quizhub.service;

import com.quizhub.dto.AttemptAnswerDto;
import com.quizhub.dto.QuizSubmissionDto;
import com.quizhub.model.Attempt;
import com.quizhub.model.AttemptAnswer;
import com.quizhub.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttemptService {
    Attempt startAttempt(Long quizId, User user);
    Optional<Attempt> findById(Long attemptId);
    AttemptAnswer saveAnswer(AttemptAnswerDto answerDto);
    Attempt submitAttempt(Long attemptId, QuizSubmissionDto submissionDto);
    List<Attempt> findUserAttempts(Long userId);
    List<Attempt> findAllAttempts();
    List<AttemptAnswer> findAttemptAnswers(Long attemptId);
    Map<Long, List<Long>> getSavedAnswersMap(Long attemptId);
    long getRemainingSeconds(Attempt attempt);
    Double getAverageScore();
    long countPassedAttempts();
}
