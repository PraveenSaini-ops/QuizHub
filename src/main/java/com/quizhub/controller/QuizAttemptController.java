package com.quizhub.controller;

import com.quizhub.dto.AttemptAnswerDto;
import com.quizhub.dto.QuizSubmissionDto;
import com.quizhub.exception.QuizNotFoundException;
import com.quizhub.exception.ResourceNotFoundException;
import com.quizhub.model.*;
import com.quizhub.service.AttemptService;
import com.quizhub.service.QuizService;
import com.quizhub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class QuizAttemptController {

    private final AttemptService attemptService;
    private final QuizService quizService;
    private final UserService userService;

    public QuizAttemptController(AttemptService attemptService,
                                 QuizService quizService,
                                 UserService userService) {
        this.attemptService = attemptService;
        this.quizService = quizService;
        this.userService = userService;
    }

    @GetMapping("/quizzes/{quizId}/take")
    public String startOrResumeQuiz(@PathVariable("quizId") Long quizId, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Quiz quiz = quizService.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));

        if (quiz.getStatus() != QuizStatus.ACTIVE) {
            redirectAttributes.addFlashAttribute("errorMessage", "This quiz is not currently active.");
            return "redirect:/quizzes";
        }

        if (quiz.getQuestions().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This quiz has no questions configured.");
            return "redirect:/quizzes";
        }

        Attempt attempt = attemptService.startAttempt(quizId, currentUser);
        return "redirect:/quizzes/" + quizId + "/attempt/" + attempt.getId();
    }

    @GetMapping("/quizzes/{quizId}/attempt/{attemptId}")
    public String takeQuiz(
            @PathVariable("quizId") Long quizId,
            @PathVariable("attemptId") Long attemptId,
            @RequestParam(value = "q", defaultValue = "0") int questionIndex,
            Model model
    ) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        Attempt attempt = attemptService.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        if (attempt.isCompleted()) {
            return "redirect:/results/" + attemptId;
        }

        Quiz quiz = attempt.getQuiz();
        List<Question> questions = quiz.getQuestions();

        if (questionIndex < 0 || questionIndex >= questions.size()) {
            questionIndex = 0;
        }

        long remainingSeconds = attemptService.getRemainingSeconds(attempt);
        Map<Long, List<Long>> savedAnswers = attemptService.getSavedAnswersMap(attemptId);

        model.addAttribute("attempt", attempt);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("currentIndex", questionIndex);
        model.addAttribute("currentQuestion", questions.get(questionIndex));
        model.addAttribute("remainingSeconds", Math.max(0, remainingSeconds));
        model.addAttribute("savedAnswers", savedAnswers);

        return "attempt/take";
    }

    @PostMapping("/api/attempt/save")
    @ResponseBody
    public ResponseEntity<?> autosaveAnswer(@RequestBody AttemptAnswerDto answerDto) {
        try {
            AttemptAnswer saved = attemptService.saveAnswer(answerDto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "questionId", saved.getQuestion().getId(),
                    "savedOptions", saved.getSelectedOptionIds()
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/quizzes/{quizId}/attempt/{attemptId}/submit")
    public String submitQuiz(
            @PathVariable("quizId") Long quizId,
            @PathVariable("attemptId") Long attemptId,
            @ModelAttribute QuizSubmissionDto submissionDto,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Attempt submitted = attemptService.submitAttempt(attemptId, submissionDto);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz submitted successfully!");
            return "redirect:/results/" + submitted.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting quiz: " + ex.getMessage());
            return "redirect:/results";
        }
    }
}
