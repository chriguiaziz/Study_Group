package com.studygroup.controller;

import com.studygroup.dto.GroupForm;
import com.studygroup.dto.MessageForm;
import com.studygroup.dto.ResourceForm;
import com.studygroup.model.AppUser;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.GroupVisibility;
import com.studygroup.model.enums.StudyFormat;
import com.studygroup.repository.CategoryRepository;
import com.studygroup.service.GroupService;
import com.studygroup.service.MessageService;
import com.studygroup.service.ResourceService;
import com.studygroup.web.AuthSession;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudyGroupController extends BaseController {

    private final GroupService groupService;
    private final CategoryRepository categoryRepository;
    private final MessageService messageService;
    private final ResourceService resourceService;

    public StudyGroupController(AuthSession authSession,
                                GroupService groupService,
                                CategoryRepository categoryRepository,
                                MessageService messageService,
                                ResourceService resourceService) {
        super(authSession);
        this.groupService = groupService;
        this.categoryRepository = categoryRepository;
        this.messageService = messageService;
        this.resourceService = resourceService;
    }

    @GetMapping("/groups")
    public String groups(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Long category,
                         Model model) {
        model.addAttribute("groups", groupService.search(q, category));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        return "groups/list";
    }

    @GetMapping("/groups/new")
    public String newGroup(HttpSession session, Model model) {
        if (currentUser(session).isEmpty()) {
            return redirectToLogin();
        }
        model.addAttribute("groupForm", new GroupForm());
        addGroupFormOptions(model);
        return "groups/form";
    }

    @PostMapping("/groups")
    public String createGroup(@Valid @ModelAttribute GroupForm groupForm,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {
        AppUser user = requireUser(session);
        if (bindingResult.hasErrors()) {
            addGroupFormOptions(model);
            return "groups/form";
        }
        StudyGroup group = groupService.createGroup(groupForm, user);
        return "redirect:/groups/" + group.getId();
    }

    @GetMapping("/groups/{id}")
    public String details(@PathVariable Long id, HttpSession session, Model model) {
        StudyGroup group = groupService.findDetailed(id);
        model.addAttribute("group", group);
        model.addAttribute("messages", messageService.groupMessages(id));
        model.addAttribute("resources", resourceService.groupResources(id));
        model.addAttribute("messageForm", new MessageForm());
        model.addAttribute("resourceForm", new ResourceForm());
        currentUser(session).ifPresent(user -> {
            model.addAttribute("isMember", group.hasMember(user));
            model.addAttribute("isOwner", group.isOwnedBy(user));
        });
        return "groups/details";
    }

    @PostMapping("/groups/{id}/join")
    public String join(@PathVariable Long id, HttpSession session) {
        groupService.joinGroup(id, requireUser(session));
        return "redirect:/groups/" + id;
    }

    @PostMapping("/groups/{id}/leave")
    public String leave(@PathVariable Long id, HttpSession session) {
        groupService.leaveGroup(id, requireUser(session));
        return "redirect:/groups/" + id;
    }

    @GetMapping("/groups/{id}/edit")
    public String edit(@PathVariable Long id, HttpSession session, Model model) {
        AppUser user = requireUser(session);
        StudyGroup group = groupService.findDetailed(id);
        if (!group.isOwnedBy(user)) {
            return "redirect:/groups/" + id;
        }
        model.addAttribute("groupForm", fromGroup(group));
        model.addAttribute("groupId", id);
        addGroupFormOptions(model);
        return "groups/form";
    }

    @PostMapping("/groups/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute GroupForm groupForm,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("groupId", id);
            addGroupFormOptions(model);
            return "groups/form";
        }
        groupService.updateGroup(id, groupForm, requireUser(session));
        return "redirect:/groups/" + id;
    }

    @PostMapping("/groups/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        groupService.deleteGroup(id, requireUser(session));
        return "redirect:/groups";
    }

    @GetMapping("/groups/{id}/chat")
    public String chat(@PathVariable Long id, HttpSession session, Model model) {
        if (currentUser(session).isEmpty()) {
            return redirectToLogin();
        }
        model.addAttribute("group", groupService.findDetailed(id));
        model.addAttribute("messages", messageService.groupMessages(id));
        model.addAttribute("messageForm", new MessageForm());
        return "groups/chat";
    }

    @PostMapping("/groups/{id}/messages")
    public String message(@PathVariable Long id,
                          @Valid @ModelAttribute MessageForm messageForm,
                          BindingResult bindingResult,
                          HttpSession session) {
        if (!bindingResult.hasErrors()) {
            messageService.sendMessage(id, requireUser(session), messageForm.getContent());
        }
        return "redirect:/groups/" + id + "/chat";
    }

    @PostMapping("/groups/{id}/resources")
    public String resource(@PathVariable Long id,
                           @Valid @ModelAttribute ResourceForm resourceForm,
                           BindingResult bindingResult,
                           HttpSession session) {
        if (!bindingResult.hasErrors()) {
            resourceService.share(id, resourceForm, requireUser(session));
        }
        return "redirect:/groups/" + id;
    }

    private void addGroupFormOptions(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("formats", StudyFormat.values());
        model.addAttribute("visibilities", GroupVisibility.values());
    }

    private GroupForm fromGroup(StudyGroup group) {
        GroupForm form = new GroupForm();
        form.setName(group.getName());
        form.setDescription(group.getDescription());
        form.setCategoryId(group.getCategory().getId());
        form.setFormat(group.getFormat());
        form.setRhythm(group.getRhythm());
        form.setSubject(group.getSubject());
        form.setVisibility(group.getVisibility());
        form.setCapacity(group.getCapacity());
        form.setFree(group.isFree());
        form.setPrice(group.getPrice());
        form.setMeetingLink(group.getMeetingLink());
        form.setLocation(group.getLocation());
        return form;
    }
}
