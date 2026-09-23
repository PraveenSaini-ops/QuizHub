package com.quizhub.controller;

import com.quizhub.dto.SearchResultDto;
import com.quizhub.model.User;
import com.quizhub.service.SearchService;
import com.quizhub.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final SearchService searchService;
    private final UserService userService;

    public SearchController(SearchService searchService, UserService userService) {
        this.searchService = searchService;
        this.userService = userService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String query, Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        SearchResultDto searchResults = searchService.globalSearch(query);
        model.addAttribute("searchQuery", query);
        model.addAttribute("searchResults", searchResults);

        return "search/results";
    }
}
