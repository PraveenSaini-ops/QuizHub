package com.quizhub.dto;

import com.quizhub.model.Question;
import com.quizhub.model.Quiz;
import com.quizhub.model.Topic;
import java.util.ArrayList;
import java.util.List;

public class SearchResultDto {

    private String query;
    private List<Topic> topics = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<Quiz> quizzes = new ArrayList<>();

    public SearchResultDto() {
    }

    public SearchResultDto(String query) {
        this.query = query;
    }

    public int getTotalCount() {
        return topics.size() + questions.size() + quizzes.size();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }
}
