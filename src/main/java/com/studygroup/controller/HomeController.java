package com.studygroup.controller;

import com.studygroup.service.GroupService;
import com.studygroup.web.AuthSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {

    private final GroupService groupService;

    public HomeController(AuthSession authSession, GroupService groupService) {
        super(authSession);
        this.groupService = groupService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredGroups", groupService.search(null, null));
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
