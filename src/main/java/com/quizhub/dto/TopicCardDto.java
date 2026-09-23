package com.quizhub.dto;

import com.quizhub.model.Topic;

public class TopicCardDto {

    private Topic topic;
    private long totalQuestions;
    private long easyCount;
    private long mediumCount;
    private long hardCount;
    private long activeQuizzesCount;
    private double averageScorePercentage;
    private int completionPercentage;

    public TopicCardDto() {
    }

    public TopicCardDto(Topic topic, long totalQuestions, long easyCount, long mediumCount, long hardCount,
                        long activeQuizzesCount, double averageScorePercentage, int completionPercentage) {
        this.topic = topic;
        this.totalQuestions = totalQuestions;
        this.easyCount = easyCount;
        this.mediumCount = mediumCount;
        this.hardCount = hardCount;
        this.activeQuizzesCount = activeQuizzesCount;
        this.averageScorePercentage = averageScorePercentage;
        this.completionPercentage = completionPercentage;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getEasyCount() {
        return easyCount;
    }

    public void setEasyCount(long easyCount) {
        this.easyCount = easyCount;
    }

    public long getMediumCount() {
        return mediumCount;
    }

    public void setMediumCount(long mediumCount) {
        this.mediumCount = mediumCount;
    }

    public long getHardCount() {
        return hardCount;
    }

    public void setHardCount(long hardCount) {
        this.hardCount = hardCount;
    }

    public long getActiveQuizzesCount() {
        return activeQuizzesCount;
    }

    public void setActiveQuizzesCount(long activeQuizzesCount) {
        this.activeQuizzesCount = activeQuizzesCount;
    }

    public double getAverageScorePercentage() {
        return averageScorePercentage;
    }

    public void setAverageScorePercentage(double averageScorePercentage) {
        this.averageScorePercentage = averageScorePercentage;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
