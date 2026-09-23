package com.quizhub.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "mcq_questions")
@DiscriminatorValue("MCQ")
public class McqQuestion extends Question {

    public McqQuestion() {
        super();
    }

    public McqQuestion(String text, String explanation, Difficulty difficulty, Topic topic) {
        super(text, explanation, difficulty, topic);
    }

    @Override
    public boolean checkAnswer(List<Long> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.size() != 1) {
            return false;
        }
        Long selectedId = selectedOptionIds.get(0);
        return getOptions().stream()
                .filter(opt -> opt.getId() != null && opt.getId().equals(selectedId))
                .findFirst()
                .map(Option::isCorrect)
                .orElse(false);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MCQ;
    }
}
