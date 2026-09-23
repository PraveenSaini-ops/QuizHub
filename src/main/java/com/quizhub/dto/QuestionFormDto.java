package com.quizhub.dto;

import com.quizhub.model.Difficulty;
import com.quizhub.model.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class QuestionFormDto {

    private Long id;

    @NotBlank(message = "Question text is required")
    private String text;

    private String explanation;

    @NotNull(message = "Topic is required")
    private Long topicId;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty = Difficulty.MEDIUM;

    @NotNull(message = "Question type is required")
    private QuestionType questionType = QuestionType.MCQ;

    private List<OptionDto> options = new ArrayList<>();

    public QuestionFormDto() {
        // Default options structure
        options.add(new OptionDto("", true));
        options.add(new OptionDto("", false));
        options.add(new OptionDto("", false));
        options.add(new OptionDto("", false));
    }

    public static class OptionDto {
        private Long id;
        private String text;
        private boolean correct;

        public OptionDto() {}

        public OptionDto(String text, boolean correct) {
            this.text = text;
            this.correct = correct;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }
}
