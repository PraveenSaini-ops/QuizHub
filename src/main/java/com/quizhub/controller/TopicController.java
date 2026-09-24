package com.quizhub.controller;

import com.quizhub.dto.TopicCardDto;
import com.quizhub.model.Topic;
import com.quizhub.model.User;
import com.quizhub.service.TopicService;
import com.quizhub.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;
    private final UserService userService;

    public TopicController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping
    public String listTopics(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        List<TopicCardDto> topicCards = topicService.findAllTopicCards();
        model.addAttribute("topicCards", topicCards);
        model.addAttribute("newTopic", new Topic());

        return "topics/list";
    }

    @PostMapping
    public String createTopic(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "openTdbCategoryId", required = false) Integer openTdbCategoryId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            topicService.createTopic(name, description, openTdbCategoryId);
            redirectAttributes.addFlashAttribute("successMessage", "Topic created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create topic: " + ex.getMessage());
        }
        return "redirect:/topics";
    }

    @PostMapping("/{id}")
    public String updateTopic(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "openTdbCategoryId", required = false) Integer openTdbCategoryId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            topicService.updateTopic(id, name, description, openTdbCategoryId);
            redirectAttributes.addFlashAttribute("successMessage", "Topic updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update topic: " + ex.getMessage());
        }
        return "redirect:/topics";
    }

    @PostMapping("/api/quick-create")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> quickCreateTopic(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description
    ) {
        if (name == null || name.trim().isEmpty()) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("error", "Topic name is required"));
        }
        try {
            Topic topic = topicService.findOrCreateTopicByName(name, description);
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of(
                    "id", topic.getId(),
                    "name", topic.getName(),
                    "description", topic.getDescription() != null ? topic.getDescription() : ""
            ));
        } catch (Exception ex) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTopic(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            topicService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Topic deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete topic: " + ex.getMessage());
        }
        return "redirect:/topics";
    }
}
