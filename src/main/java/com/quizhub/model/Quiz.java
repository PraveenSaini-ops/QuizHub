package com.quizhub.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private int durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizStatus status = QuizStatus.ACTIVE;

    @Column(nullable = false)
    private int passPercentage = 60;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @OrderColumn(name = "question_order")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attempt> attempts = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String accessCode;

    @Column(nullable = true)
    private String accessPassword;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Quiz() {
    }

    public Quiz(String title, String description, Topic topic, int durationMinutes, QuizStatus status) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    public Quiz(String title, String description, Topic topic, int durationMinutes, QuizStatus status, String accessCode, String accessPassword) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.accessCode = accessCode;
        this.accessPassword = accessPassword;
    }

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

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public QuizStatus getStatus() {
        return status;
    }

    public void setStatus(QuizStatus status) {
        this.status = status;
    }

    public int getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(int passPercentage) {
        this.passPercentage = passPercentage;
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

    public boolean hasPassword() {
        return accessPassword != null && !accessPassword.trim().isEmpty();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
