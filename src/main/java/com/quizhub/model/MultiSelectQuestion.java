package com.quizhub.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "multi_select_questions")
@DiscriminatorValue("MULTI_SELECT")
public class MultiSelectQuestion extends Question {

    public MultiSelectQuestion() {
        super();
    }

    public MultiSelectQuestion(String text, String explanation, Difficulty difficulty, Topic topic) {
        super(text, explanation, difficulty, topic);
    }

    @Override
    public boolean checkAnswer(List<Long> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return false;
        }

        Set<Long> correctOptionIds = getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(Collectors.toSet());

        if (correctOptionIds.isEmpty()) {
            return false;
        }

        Set<Long> selectedSet = new HashSet<>(selectedOptionIds);
        return selectedSet.equals(correctOptionIds);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTI_SELECT;
    }
}
