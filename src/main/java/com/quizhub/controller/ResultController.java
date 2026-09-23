package com.quizhub.controller;

import com.quizhub.exception.ResourceNotFoundException;
import com.quizhub.model.*;
import com.quizhub.service.AttemptService;
import com.quizhub.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/results")
public class ResultController {

    private final AttemptService attemptService;
    private final UserService userService;

    public ResultController(AttemptService attemptService, UserService userService) {
        this.attemptService = attemptService;
        this.userService = userService;
    }

    @GetMapping
    public String listResults(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        List<Attempt> attempts;
        if (currentUser != null && currentUser.getRole() == Role.ROLE_ADMIN) {
            attempts = attemptService.findAllAttempts();
        } else if (currentUser != null) {
            attempts = attemptService.findUserAttempts(currentUser.getId());
        } else {
            attempts = Collections.emptyList();
        }

        long passedCount = attempts.stream().filter(Attempt::isPassed).count();
        double avgScore = attempts.stream()
                .filter(a -> a.getScorePercentage() != null)
                .mapToDouble(Attempt::getScorePercentage)
                .average()
                .orElse(0.0);

        model.addAttribute("attempts", attempts);
        model.addAttribute("passedCount", passedCount);
        model.addAttribute("totalAttempts", attempts.size());
        model.addAttribute("avgScore", Math.round(avgScore * 10.0) / 10.0);

        return "results/list";
    }

    @GetMapping("/{attemptId}")
    public String viewResultDetails(@PathVariable("attemptId") Long attemptId, Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        Attempt attempt = attemptService.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        List<AttemptAnswer> answers = attemptService.findAttemptAnswers(attemptId);
        
        // Map answer by question id for easy review lookup
        Map<Long, AttemptAnswer> answerMap = new HashMap<>();
        for (AttemptAnswer a : answers) {
            answerMap.put(a.getQuestion().getId(), a);
        }

        // Compute per-topic breakdown
        Map<String, TopicScoreStat> topicBreakdown = new HashMap<>();
        for (Question q : attempt.getQuiz().getQuestions()) {
            String topicName = q.getTopic().getName();
            TopicScoreStat stat = topicBreakdown.computeIfAbsent(topicName, k -> new TopicScoreStat());
            stat.total++;
            AttemptAnswer ans = answerMap.get(q.getId());
            if (ans != null && ans.isCorrect()) {
                stat.correct++;
            }
        }

        model.addAttribute("attempt", attempt);
        model.addAttribute("quiz", attempt.getQuiz());
        model.addAttribute("answers", answers);
        model.addAttribute("answerMap", answerMap);
        model.addAttribute("topicBreakdown", topicBreakdown);

        return "results/review";
    }

    public static class TopicScoreStat {
        public int total = 0;
        public int correct = 0;

        public int getPercentage() {
            return total > 0 ? (int) Math.round(((double) correct / total) * 100.0) : 0;
        }

        public int getTotal() {
            return total;
        }

        public int getCorrect() {
            return correct;
        }
    }
}
