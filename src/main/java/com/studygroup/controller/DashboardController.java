package com.studygroup.controller;

import com.studygroup.model.AppUser;
import com.studygroup.service.GroupService;
import com.studygroup.service.MessageService;
import com.studygroup.service.NotificationService;
import com.studygroup.web.AuthSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends BaseController {

    private final GroupService groupService;
    private final MessageService messageService;
    private final NotificationService notificationService;

    public DashboardController(AuthSession authSession,
                               GroupService groupService,
                               MessageService messageService,
                               NotificationService notificationService) {
        super(authSession);
        this.groupService = groupService;
        this.messageService = messageService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        AppUser user = requireUser(session);
        model.addAttribute("joinedGroups", groupService.joinedGroups(user.getId()));
        model.addAttribute("recentMessages", messageService.recentForUser(user.getId()));
        model.addAttribute("notifications", notificationService.forUser(user.getId()).stream().limit(5).toList());
        return "dashboard";
    }
}
