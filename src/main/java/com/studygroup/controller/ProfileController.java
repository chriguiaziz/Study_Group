package com.studygroup.controller;

import com.studygroup.dto.ProfileForm;
import com.studygroup.model.AppUser;
import com.studygroup.service.GroupService;
import com.studygroup.service.UserService;
import com.studygroup.web.AuthSession;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController extends BaseController {

    private final UserService userService;
    private final GroupService groupService;

    public ProfileController(AuthSession authSession, UserService userService, GroupService groupService) {
        super(authSession);
        this.userService = userService;
        this.groupService = groupService;
    }

    @GetMapping("/profile")
    public String myProfile(HttpSession session) {
        AppUser user = requireUser(session);
        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable Long id, Model model) {
        AppUser user = userService.findById(id);
        model.addAttribute("profileUser", user);
        model.addAttribute("groups", groupService.joinedGroups(id));
        return "profile/view";
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model) {
        AppUser user = requireUser(session);
        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        form.setUniversity(user.getUniversity());
        form.setMajor(user.getMajor());
        form.setBio(user.getBio());
        form.setAvatarUrl(user.getAvatarUrl());
        model.addAttribute("profileForm", form);
        return "profile/settings";
    }

    @PostMapping("/settings")
    public String updateSettings(@Valid ProfileForm profileForm,
                                 BindingResult bindingResult,
                                 HttpSession session) {
        AppUser user = requireUser(session);
        if (bindingResult.hasErrors()) {
            return "profile/settings";
        }
        userService.updateProfile(user.getId(), profileForm);
        return "redirect:/profile";
    }
}
