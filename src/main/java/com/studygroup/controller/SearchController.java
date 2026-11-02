package com.studygroup.controller;

import com.studygroup.service.GroupService;
import com.studygroup.service.UserService;
import com.studygroup.web.AuthSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController extends BaseController {

    private final GroupService groupService;
    private final UserService userService;

    public SearchController(AuthSession authSession, GroupService groupService, UserService userService) {
        super(authSession);
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("groups", groupService.search(q, null));
        model.addAttribute("users", q == null || q.isBlank() ? userService.findAll() : userService.search(q));
        return "search";
    }
}
