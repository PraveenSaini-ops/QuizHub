package com.quizhub.controller;

import com.quizhub.dto.QuizCreateDto;
import com.quizhub.model.*;
import com.quizhub.service.QuestionService;
import com.quizhub.service.QuizService;
import com.quizhub.service.TopicService;
import com.quizhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final TopicService topicService;
    private final QuestionService questionService;
    private final UserService userService;

    public QuizController(QuizService quizService,
                          TopicService topicService,
                          QuestionService questionService,
                          UserService userService) {
        this.quizService = quizService;
        this.topicService = topicService;
        this.questionService = questionService;
        this.userService = userService;
    }

    @GetMapping
    public String listQuizzes(
            @RequestParam(value = "topicId", required = false) Long topicId,
            @RequestParam(value = "status", required = false) QuizStatus status,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "9") int size,
            Model model
    ) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        // Enforce ACTIVE only status for students
        if (currentUser != null && currentUser.getRole() == Role.ROLE_STUDENT) {
            status = QuizStatus.ACTIVE;
        }

        Page<Quiz> quizPage = quizService.findFilteredQuizzes(
                topicId, status, search, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        model.addAttribute("quizPage", quizPage);
        model.addAttribute("topics", topicService.findAll());
        model.addAttribute("selectedTopicId", topicId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchQuery", search);
        model.addAttribute("statuses", QuizStatus.values());

        return "quizzes/list";
    }

    @GetMapping("/live")
    public String liveQuizzes(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        List<Quiz> liveQuizzes = quizService.findActiveQuizzes();
        model.addAttribute("liveQuizzes", liveQuizzes);
        model.addAttribute("topics", topicService.findAll());

        return "quizzes/live";
    }

    @GetMapping("/create")
    public String createQuizForm(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        List<Topic> topics = topicService.findAll();
        model.addAttribute("topics", topics);
        model.addAttribute("quizForm", new QuizCreateDto());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("statuses", QuizStatus.values());

        return "quizzes/create";
    }

    @GetMapping("/questions-by-topic")
    @ResponseBody
    public List<Question> getQuestionsForTopic(
            @RequestParam("topicId") Long topicId,
            @RequestParam(value = "difficulty", required = false) Difficulty difficulty
    ) {
        if (difficulty != null) {
            return questionService.findByTopicIdAndDifficulty(topicId, difficulty);
        }
        return questionService.findByTopicId(topicId);
    }

    @PostMapping
    public String createQuiz(
            @Valid @ModelAttribute("quizForm") QuizCreateDto formDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("topics", topicService.findAll());
            model.addAttribute("difficulties", Difficulty.values());
            model.addAttribute("statuses", QuizStatus.values());
            return "quizzes/create";
        }
        try {
            Quiz quiz = quizService.createQuiz(formDto);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz \"" + quiz.getTitle() + "\" created successfully with " + quiz.getQuestions().size() + " questions!");
            return "redirect:/quizzes";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create quiz: " + ex.getMessage());
            return "redirect:/quizzes/create";
        }
    }

    @GetMapping("/{id}")
    public String viewQuizDetails(@PathVariable("id") Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        Quiz quiz = quizService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

        model.addAttribute("quiz", quiz);
        return "quizzes/details";
    }

    @PostMapping("/{id}/status")
    public String toggleQuizStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") QuizStatus status,
            RedirectAttributes redirectAttributes
    ) {
        try {
            quizService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz status updated to " + status);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update status: " + ex.getMessage());
        }
        return "redirect:/quizzes";
    }

    @PostMapping("/{id}/delete")
    public String deleteQuiz(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            quizService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete quiz: " + ex.getMessage());
        }
        return "redirect:/quizzes";
    }
}
