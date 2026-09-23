package com.quizhub.controller;

import com.quizhub.model.Difficulty;
import com.quizhub.model.Question;
import com.quizhub.service.OpenTdbImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/import")
public class ImportController {

    private final OpenTdbImportService openTdbImportService;

    public ImportController(OpenTdbImportService openTdbImportService) {
        this.openTdbImportService = openTdbImportService;
    }

    @PostMapping("/questions")
    public String importQuestionsForm(
            @RequestParam("topicId") Long topicId,
            @RequestParam(value = "amount", defaultValue = "10") int amount,
            @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
            RedirectAttributes redirectAttributes
    ) {
        try {
            List<Question> imported = openTdbImportService.importQuestions(topicId, amount, difficulty);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully imported " + imported.size() + " questions from Open Trivia DB!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Import failed: " + ex.getMessage());
        }
        return "redirect:/questions";
    }

    @PostMapping("/questions/json")
    @ResponseBody
    public ResponseEntity<?> importQuestionsJson(
            @RequestParam("topicId") Long topicId,
            @RequestParam(value = "amount", defaultValue = "10") int amount,
            @RequestParam(value = "difficulty", required = false) Difficulty difficulty
    ) {
        try {
            List<Question> imported = openTdbImportService.importQuestions(topicId, amount, difficulty);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", imported.size()
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", ex.getMessage()
            ));
        }
    }
}
