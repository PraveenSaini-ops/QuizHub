package com.quizhub.model;

public enum QuestionType {
    MCQ("Single Choice"),
    TRUE_FALSE("True / False"),
    MULTI_SELECT("Multiple Choice");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
