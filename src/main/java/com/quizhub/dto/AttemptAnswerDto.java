package com.quizhub.dto;

import java.util.ArrayList;
import java.util.List;

public class AttemptAnswerDto {

    private Long attemptId;
    private Long questionId;
    private List<Long> selectedOptionIds = new ArrayList<>();

    public AttemptAnswerDto() {
    }

    public AttemptAnswerDto(Long attemptId, Long questionId, List<Long> selectedOptionIds) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOptionIds = selectedOptionIds;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Long> getSelectedOptionIds() {
        return selectedOptionIds;
    }

    public void setSelectedOptionIds(List<Long> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }
}
