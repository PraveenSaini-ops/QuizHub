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

    @GetMapping("/quizzes/join")
    public String searchAndJoinQuiz(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "link", required = false) String link,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);

        String input = code != null && !code.trim().isEmpty() ? code : link;
        if (input != null && !input.trim().isEmpty()) {
            String extractedCode = extractAccessCode(input.trim());
            return "redirect:/quizzes/join/" + extractedCode;
        }

        return "quizzes/join";
    }

    @GetMapping("/quizzes/join/{accessCode}")
    public String viewJoinQuizPage(
            @PathVariable("accessCode") String accessCode,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);

        String cleanedCode = extractAccessCode(accessCode);
        Quiz quiz = quizService.findByAccessCode(cleanedCode).orElse(null);

        if (quiz == null) {
            model.addAttribute("errorMessage", "No quiz found with code or link \"" + accessCode + "\". Please check with your instructor.");
            return "quizzes/join";
        }

        if (currentUser.getRole() == Role.ROLE_STUDENT && quiz.getStatus() != QuizStatus.ACTIVE) {
            model.addAttribute("errorMessage", "This quiz is currently not active.");
            return "quizzes/join";
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("accessCode", cleanedCode);
        return "quizzes/join";
    }

    @PostMapping("/quizzes/join/{accessCode}")
    public String processJoinQuiz(
            @PathVariable("accessCode") String accessCode,
            @RequestParam(value = "password", required = false) String password,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        String cleanedCode = extractAccessCode(accessCode);
        Quiz quiz = quizService.findByAccessCode(cleanedCode)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with code: " + accessCode));

        if (currentUser.getRole() == Role.ROLE_STUDENT && quiz.getStatus() != QuizStatus.ACTIVE) {
            redirectAttributes.addFlashAttribute("errorMessage", "This quiz is not currently active.");
            return "redirect:/quizzes/join/" + cleanedCode;
        }

        if (quiz.getQuestions().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This quiz has no questions configured yet.");
            return "redirect:/quizzes/join/" + cleanedCode;
        }

        // Validate password if quiz is password protected
        if (quiz.hasPassword()) {
            String enteredPassword = password != null ? password.trim() : "";
            if (!quiz.getAccessPassword().equals(enteredPassword)) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("quiz", quiz);
                model.addAttribute("accessCode", cleanedCode);
                model.addAttribute("errorMessage", "Incorrect quiz password. Please check with your instructor.");
                return "quizzes/join";
            }
        }

        Attempt attempt = attemptService.startAttempt(quiz.getId(), currentUser);
        return "redirect:/quizzes/" + quiz.getId() + "/attempt/" + attempt.getId();
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

        // If quiz has password, redirect to join screen for password entry
        if (quiz.hasPassword()) {
            return "redirect:/quizzes/join/" + quiz.getAccessCode();
        }

        Attempt attempt = attemptService.startAttempt(quizId, currentUser);
        return "redirect:/quizzes/" + quizId + "/attempt/" + attempt.getId();
    }

    private String extractAccessCode(String input) {
        if (input == null) return "";
        String trimmed = input.trim();
        if (trimmed.contains("/")) {
            trimmed = trimmed.substring(trimmed.lastIndexOf('/') + 1);
        }
        if (trimmed.contains("?")) {
            trimmed = trimmed.substring(0, trimmed.indexOf('?'));
        }
        return trimmed.trim().toUpperCase();
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
