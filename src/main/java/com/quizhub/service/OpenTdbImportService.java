package com.quizhub.service;

import com.quizhub.model.Difficulty;
import com.quizhub.model.Question;
import java.util.List;

public interface OpenTdbImportService {
    List<Question> importQuestions(Long topicId, int amount, Difficulty difficulty);
}
