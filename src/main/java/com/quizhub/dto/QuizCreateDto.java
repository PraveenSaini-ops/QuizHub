package com.quizhub.dto;

import com.quizhub.model.Difficulty;
import com.quizhub.model.QuizStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class QuizCreateDto {

    private Long id;

    @NotBlank(message = "Quiz title is required")
    private String title;

    private String description;

    private Long topicId;

    private String newTopicName;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes = 15;

    @Min(value = 1, message = "Pass percentage must be between 1 and 100")
    private int passPercentage = 60;

    private QuizStatus status = QuizStatus.ACTIVE;

    // "MANUAL" or "AUTO"
    private String selectionMode = "AUTO";

    private Difficulty difficultyFilter;

    private int autoQuestionCount = 10;

    private List<Long> selectedQuestionIds = new ArrayList<>();

    private String accessCode;

    private String accessPassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getNewTopicName() {
        return newTopicName;
    }

    public void setNewTopicName(String newTopicName) {
        this.newTopicName = newTopicName;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(int passPercentage) {
        this.passPercentage = passPercentage;
    }

    public QuizStatus getStatus() {
        return status;
    }

    public void setStatus(QuizStatus status) {
        this.status = status;
    }

    public String getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(String selectionMode) {
        this.selectionMode = selectionMode;
    }

    public Difficulty getDifficultyFilter() {
        return difficultyFilter;
    }

    public void setDifficultyFilter(Difficulty difficultyFilter) {
        this.difficultyFilter = difficultyFilter;
    }

    public int getAutoQuestionCount() {
        return autoQuestionCount;
    }

    public void setAutoQuestionCount(int autoQuestionCount) {
        this.autoQuestionCount = autoQuestionCount;
    }

    public List<Long> getSelectedQuestionIds() {
        return selectedQuestionIds;
    }

    public void setSelectedQuestionIds(List<Long> selectedQuestionIds) {
        this.selectedQuestionIds = selectedQuestionIds;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getAccessPassword() {
        return accessPassword;
    }

    public void setAccessPassword(String accessPassword) {
        this.accessPassword = accessPassword;
    }
}
