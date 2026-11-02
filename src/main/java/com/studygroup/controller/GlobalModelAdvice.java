package com.studygroup.controller;

import com.studygroup.model.AppUser;
import com.studygroup.service.NotificationService;
import com.studygroup.web.AuthSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final AuthSession authSession;
    private final NotificationService notificationService;

    public GlobalModelAdvice(AuthSession authSession, NotificationService notificationService) {
        this.authSession = authSession;
        this.notificationService = notificationService;
    }

    @ModelAttribute("currentUser")
    public AppUser currentUser(HttpSession session) {
        return authSession.currentUser(session).orElse(null);
    }

    @ModelAttribute("unreadCount")
    public long unreadCount(HttpSession session) {
        return authSession.currentUser(session)
                .map(user -> notificationService.unreadCount(user.getId()))
                .orElse(0L);
    }
}
