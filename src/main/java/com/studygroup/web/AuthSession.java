package com.studygroup.web;

import com.studygroup.model.AppUser;
import com.studygroup.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthSession {

    public static final String USER_ID = "STUDY_GROUP_USER_ID";

    private final UserService userService;

    public AuthSession(UserService userService) {
        this.userService = userService;
    }

    public void signIn(HttpSession session, AppUser user) {
        session.setAttribute(USER_ID, user.getId());
    }

    public void signOut(HttpSession session) {
        session.invalidate();
    }

    public Optional<AppUser> currentUser(HttpSession session) {
        Object value = session.getAttribute(USER_ID);
        if (!(value instanceof Long userId)) {
            return Optional.empty();
        }
        try {
            return Optional.of(userService.findById(userId));
        } catch (IllegalArgumentException exception) {
            session.removeAttribute(USER_ID);
            return Optional.empty();
        }
    }

    public AppUser requireUser(HttpSession session) {
        return currentUser(session).orElseThrow(() -> new IllegalStateException("Please login first."));
    }
}
