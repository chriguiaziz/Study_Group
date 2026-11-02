package com.studygroup.controller;

import com.studygroup.dto.LoginForm;
import com.studygroup.dto.RegisterForm;
import com.studygroup.model.AppUser;
import com.studygroup.service.UserService;
import com.studygroup.web.AuthSession;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController extends BaseController {

    private final UserService userService;
    private final AuthSession authSession;

    public AuthController(AuthSession authSession, UserService userService) {
        super(authSession);
        this.authSession = authSession;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        return userService.authenticate(loginForm.getEmail(), loginForm.getPassword())
                .map(user -> {
                    authSession.signIn(session, user);
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("authError", "Invalid email or password.");
                    return "auth/login";
                });
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm registerForm,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            AppUser user = userService.register(registerForm);
            authSession.signIn(session, user);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("authError", exception.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        authSession.signOut(session);
        return "redirect:/";
    }
}
