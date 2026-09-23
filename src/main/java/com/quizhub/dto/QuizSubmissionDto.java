package com.quizhub.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizSubmissionDto {

    private Long attemptId;
    // Map of questionId -> list of selectedOptionIds
    private Map<Long, List<Long>> answers = new HashMap<>();

    public QuizSubmissionDto() {
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Map<Long, List<Long>> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, List<Long>> answers) {
        this.answers = answers;
    }
}
