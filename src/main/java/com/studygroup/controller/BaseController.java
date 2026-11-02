package com.studygroup.controller;

import com.studygroup.model.AppUser;
import com.studygroup.web.AuthSession;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public abstract class BaseController {

    private final AuthSession authSession;

    protected BaseController(AuthSession authSession) {
        this.authSession = authSession;
    }

    protected Optional<AppUser> currentUser(HttpSession session) {
        return authSession.currentUser(session);
    }

    protected AppUser requireUser(HttpSession session) {
        return authSession.requireUser(session);
    }

    protected String redirectToLogin() {
        return "redirect:/login";
    }
}
