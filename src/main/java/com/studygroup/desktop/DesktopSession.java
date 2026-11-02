package com.studygroup.desktop;

import com.studygroup.model.AppUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DesktopSession {

    private AppUser currentUser;

    public Optional<AppUser> currentUser() {
        return Optional.ofNullable(currentUser);
    }

    public AppUser requireUser() {
        return currentUser().orElseThrow(() -> new IllegalStateException("Please login first."));
    }

    public void signIn(AppUser user) {
        this.currentUser = user;
    }

    public void signOut() {
        currentUser = null;
    }

    public boolean isSignedIn() {
        return currentUser != null;
    }
}
