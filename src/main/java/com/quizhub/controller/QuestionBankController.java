package com.quizhub.controller;

import com.quizhub.dto.QuestionFormDto;
import com.quizhub.model.Difficulty;
import com.quizhub.model.Question;
import com.quizhub.model.QuestionType;
import com.quizhub.model.Topic;
import com.quizhub.model.User;
import com.quizhub.service.QuestionService;
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
@RequestMapping("/questions")
public class QuestionBankController {

    private final QuestionService questionService;
    private final TopicService topicService;
    private final UserService userService;

    public QuestionBankController(QuestionService questionService,
                                  TopicService topicService,
                                  UserService userService) {
        this.questionService = questionService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping
    public String listQuestions(
            @RequestParam(value = "topicId", required = false) Long topicId,
            @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        Page<Question> questionPage = questionService.findFilteredQuestions(
                topicId, difficulty, search, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<Topic> topics = topicService.findAll();

        model.addAttribute("questionPage", questionPage);
        model.addAttribute("topics", topics);
        model.addAttribute("selectedTopicId", topicId);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("searchQuery", search);
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("questionTypes", QuestionType.values());
        model.addAttribute("newQuestionForm", new QuestionFormDto());

        return "questions/list";
    }

    @PostMapping
    public String createQuestion(
            @Valid @ModelAttribute("newQuestionForm") QuestionFormDto formDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors in the question form.");
            return "redirect:/questions";
        }
        try {
            questionService.createQuestionFromDto(formDto);
            redirectAttributes.addFlashAttribute("successMessage", "Question created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating question: " + ex.getMessage());
        }
        return "redirect:/questions";
    }

    @GetMapping("/{id}/edit")
    @ResponseBody
    public QuestionFormDto getQuestionForEdit(@PathVariable("id") Long id) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));
        return questionService.toFormDto(question);
    }

    @PostMapping("/{id}")
    public String updateQuestion(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("editQuestionForm") QuestionFormDto formDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation errors when updating question.");
            return "redirect:/questions";
        }
        try {
            questionService.updateQuestionFromDto(id, formDto);
            redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating question: " + ex.getMessage());
        }
        return "redirect:/questions";
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            questionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete question: " + ex.getMessage());
        }
        return "redirect:/questions";
    }
}
