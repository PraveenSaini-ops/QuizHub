package com.quizhub.controller;

import com.quizhub.dto.TopicCardDto;
import com.quizhub.model.Attempt;
import com.quizhub.model.Quiz;
import com.quizhub.model.QuizStatus;
import com.quizhub.model.User;
import com.quizhub.service.AttemptService;
import com.quizhub.service.QuestionService;
import com.quizhub.service.QuizService;
import com.quizhub.service.TopicService;
import com.quizhub.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final QuestionService questionService;
    private final QuizService quizService;
    private final TopicService topicService;
    private final AttemptService attemptService;
    private final UserService userService;

    public DashboardController(QuestionService questionService,
                               QuizService quizService,
                               TopicService topicService,
                               AttemptService attemptService,
                               UserService userService) {
        this.questionService = questionService;
        this.quizService = quizService;
        this.topicService = topicService;
        this.attemptService = attemptService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        boolean isAdmin = currentUser != null && currentUser.getRole() == com.quizhub.model.Role.ROLE_ADMIN;
        long activeQuizzes = quizService.countByStatus(QuizStatus.ACTIVE);
        model.addAttribute("activeQuizzes", activeQuizzes);

        if (isAdmin) {
            // Admin Metrics & All Activity
            long totalQuestions = questionService.count();
            long totalQuizzes = quizService.count();
            Double averageScore = attemptService.getAverageScore();

            model.addAttribute("totalQuestions", totalQuestions);
            model.addAttribute("totalQuizzes", totalQuizzes);
            model.addAttribute("averageScore", averageScore != null ? averageScore : 0.0);

            List<Quiz> recentQuizzes = quizService.findAll().stream()
                    .limit(6)
                    .toList();
            model.addAttribute("recentQuizzes", recentQuizzes);

            List<Attempt> recentAttempts = attemptService.findAllAttempts().stream()
                    .filter(Attempt::isCompleted)
                    .limit(5)
                    .toList();
            model.addAttribute("recentAttempts", recentAttempts);
        } else {
            // Student Specific Metrics & Personal Activity
            List<Attempt> myAttempts = currentUser != null
                    ? attemptService.findUserAttempts(currentUser.getId()).stream().filter(Attempt::isCompleted).toList()
                    : List.of();

            long myCompletedCount = myAttempts.size();
            long myPassedCount = myAttempts.stream().filter(Attempt::isPassed).count();
            double myAvgScore = myAttempts.stream()
                    .filter(a -> a.getScorePercentage() != null)
                    .mapToDouble(Attempt::getScorePercentage)
                    .average()
                    .orElse(0.0);

            model.addAttribute("myCompletedCount", myCompletedCount);
            model.addAttribute("myPassedCount", myPassedCount);
            model.addAttribute("myAvgScore", Math.round(myAvgScore * 10.0) / 10.0);

            // Students only see ACTIVE quizzes available to take
            List<Quiz> recentQuizzes = quizService.findActiveQuizzes().stream()
                    .limit(6)
                    .toList();
            model.addAttribute("recentQuizzes", recentQuizzes);

            // Students only see their own recent attempts
            List<Attempt> recentAttempts = myAttempts.stream()
                    .limit(5)
                    .toList();
            model.addAttribute("recentAttempts", recentAttempts);
        }

        // Topic Overview
        List<TopicCardDto> topicCards = topicService.findAllTopicCards();
        model.addAttribute("topicCards", topicCards);

        return "dashboard/index";
    }
}
