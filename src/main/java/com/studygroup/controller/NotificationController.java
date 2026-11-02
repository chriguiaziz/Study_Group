package com.studygroup.controller;

import com.studygroup.model.AppUser;
import com.studygroup.service.NotificationService;
import com.studygroup.web.AuthSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    public NotificationController(AuthSession authSession, NotificationService notificationService) {
        super(authSession);
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        AppUser user = requireUser(session);
        model.addAttribute("notifications", notificationService.forUser(user.getId()));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return "redirect:/notifications";
    }
}
