package com.quizhub.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attempt_answers")
public class AttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "attempt_answer_selected_options", joinColumns = @JoinColumn(name = "attempt_answer_id"))
    @Column(name = "option_id")
    private List<Long> selectedOptionIds = new ArrayList<>();

    private boolean isCorrect;

    public AttemptAnswer() {
    }

    public AttemptAnswer(Attempt attempt, Question question, List<Long> selectedOptionIds, boolean isCorrect) {
        this.attempt = attempt;
        this.question = question;
        this.selectedOptionIds = selectedOptionIds != null ? selectedOptionIds : new ArrayList<>();
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Attempt getAttempt() {
        return attempt;
    }

    public void setAttempt(Attempt attempt) {
        this.attempt = attempt;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Long> getSelectedOptionIds() {
        return selectedOptionIds;
    }

    public void setSelectedOptionIds(List<Long> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
