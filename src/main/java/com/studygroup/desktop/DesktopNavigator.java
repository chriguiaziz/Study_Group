package com.studygroup.desktop;

import com.studygroup.dto.GroupForm;
import com.studygroup.dto.ProfileForm;
import com.studygroup.dto.RegisterForm;
import com.studygroup.dto.ResourceForm;
import com.studygroup.model.AppUser;
import com.studygroup.model.Category;
import com.studygroup.model.LearningResource;
import com.studygroup.model.Message;
import com.studygroup.model.Notification;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.GroupVisibility;
import com.studygroup.model.enums.ResourceType;
import com.studygroup.model.enums.StudyFormat;
import com.studygroup.model.enums.StudyRhythm;
import com.studygroup.repository.CategoryRepository;
import com.studygroup.service.GroupService;
import com.studygroup.service.MessageService;
import com.studygroup.service.NotificationService;
import com.studygroup.service.ResourceService;
import com.studygroup.service.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DesktopNavigator {

    private final DesktopSession session;
    private final UserService userService;
    private final GroupService groupService;
    private final MessageService messageService;
    private final ResourceService resourceService;
    private final NotificationService notificationService;
    private final CategoryRepository categoryRepository;
    private Stage stage;

    public DesktopNavigator(DesktopSession session,
                            UserService userService,
                            GroupService groupService,
                            MessageService messageService,
                            ResourceService resourceService,
                            NotificationService notificationService,
                            CategoryRepository categoryRepository) {
        this.session = session;
        this.userService = userService;
        this.groupService = groupService;
        this.messageService = messageService;
        this.resourceService = resourceService;
        this.notificationService = notificationService;
        this.categoryRepository = categoryRepository;
    }

    public void attach(Stage stage) {
        this.stage = stage;
        stage.setTitle("StudyGroup Desktop");
        stage.setMinWidth(1040);
        stage.setMinHeight(720);
        showLogin();
        stage.show();
    }

    private void setContent(String title, Node content, boolean sidebar) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(topbar());
        if (sidebar && session.isSignedIn()) {
            root.setLeft(sidebar());
        }
        VBox page = new VBox(18, content);
        page.getStyleClass().add(sidebar || session.isSignedIn() ? "page" : "auth-page");
        ScrollPane scrollPane = UiFactory.scroll(page);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1180, 760);
        String stylesheet = getClass().getResource("/static/css/desktop.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        stage.setTitle(title + " - StudyGroup");
        stage.setScene(scene);
    }

    private HBox topbar() {
        Label brand = new Label("StudyGroup");
        brand.getStyleClass().add("brand");
        Label subtitle = new Label("IHEC collaborative desktop");
        subtitle.getStyleClass().add("brand-subtitle");
        VBox brandBlock = new VBox(1, brand, subtitle);
        HBox bar = new HBox(12, brandBlock, UiFactory.spacer());
        bar.getStyleClass().add("topbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        if (session.isSignedIn()) {
            Button profile = UiFactory.secondaryButton(session.requireUser().getInitials());
            profile.setOnAction(event -> showProfile());
            Button logout = UiFactory.secondaryButton("Logout");
            logout.setOnAction(event -> {
                session.signOut();
                showLogin();
            });
            bar.getChildren().addAll(profile, logout);
        }
        return bar;
    }

    private VBox sidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        Button dashboard = UiFactory.navButton("Dashboard");
        dashboard.setOnAction(event -> showDashboard());
        Button groups = UiFactory.navButton("Explore Groups");
        groups.setOnAction(event -> showGroups());
        Button create = UiFactory.navButton("Create group");
        create.setOnAction(event -> showGroupForm());
        Button search = UiFactory.navButton("Search");
        search.setOnAction(event -> showSearch());
        Button notifications = UiFactory.navButton("Notifications");
        notifications.setOnAction(event -> showNotifications());
        Button profile = UiFactory.navButton("Profile");
        profile.setOnAction(event -> showProfile());
        Button settings = UiFactory.navButton("Settings");
        settings.setOnAction(event -> showSettings());
        sidebar.getChildren().addAll(dashboard, groups, create, search, notifications, profile, settings);
        return sidebar;
    }

    private void showHome() {
        VBox hero = UiFactory.card(
                UiFactory.badge("Desktop university collaboration"),
                UiFactory.title("StudyGroup"),
                UiFactory.muted("Create study groups, join classmates, chat, share files or links, organize Zoom/local sessions, and manage collaborative learning from a desktop Java application.")
        );
        Button explore = UiFactory.primaryButton("Explore groups");
        explore.setOnAction(event -> showGroups());
        Button create = UiFactory.secondaryButton("Create account");
        create.setOnAction(event -> showRegister());
        hero.getChildren().add(UiFactory.row(explore, create));

        FlowPane featured = cardFlow();
        groupService.search(null, null).forEach(group -> featured.getChildren().add(groupCard(group)));
        VBox content = new VBox(20, hero, UiFactory.sectionTitle("Featured groups"), featured);
        setContent("Home", content, false);
    }

    private void showLogin() {
        TextField email = UiFactory.textField("Email");
        PasswordField password = UiFactory.passwordField("Password");
        Button submit = UiFactory.primaryButton("Login");
        submit.setOnAction(event -> userService.authenticate(email.getText(), password.getText())
                .ifPresentOrElse(user -> {
                    session.signIn(user);
                    showDashboard();
                }, () -> showError("Login failed", "Invalid email or password.")));
        Button demo = UiFactory.secondaryButton("Use demo account");
        demo.setOnAction(event -> {
            email.setText("lina@student.edu");
            password.setText("1234");
        });
        Button register = UiFactory.secondaryButton("Create account");
        register.setOnAction(event -> showRegister());
        Button about = UiFactory.secondaryButton("About");
        about.setOnAction(event -> showAbout());
        VBox form = UiFactory.card(
                UiFactory.greenBadge("Welcome back"),
                UiFactory.title("Login"),
                UiFactory.muted("Sign in to manage your IHEC study groups, discussions, resources and notifications."),
                field("Email", email),
                field("Password", password),
                UiFactory.row(submit, demo),
                UiFactory.muted("No account yet?"),
                UiFactory.row(register, about)
        );
        form.setMaxWidth(520);
        setContent("Login", authShell(authVisual(), form), false);
    }

    private void showRegister() {
        TextField name = UiFactory.textField("Full name");
        TextField email = UiFactory.textField("Email");
        PasswordField password = UiFactory.passwordField("Password");
        TextField university = UiFactory.textField("University");
        TextField major = UiFactory.textField("Major");
        university.setText("IHEC Carthage");
        Button submit = UiFactory.primaryButton("Register");
        submit.setOnAction(event -> runSafely("Register", () -> {
            RegisterForm form = new RegisterForm();
            form.setFullName(name.getText());
            form.setEmail(email.getText());
            form.setPassword(password.getText());
            form.setUniversity(university.getText());
            form.setMajor(major.getText());
            session.signIn(userService.register(form));
            showDashboard();
        }));
        Button login = UiFactory.secondaryButton("I already have an account");
        login.setOnAction(event -> showLogin());
        Button about = UiFactory.secondaryButton("About");
        about.setOnAction(event -> showAbout());
        VBox form = UiFactory.card(
                UiFactory.goldBadge("New student"),
                UiFactory.title("Create account"),
                UiFactory.muted("Create your StudyGroup account for IHEC collaboration."),
                field("Full name", name),
                field("Email", email),
                field("Password", password),
                field("University", university),
                field("Major", major),
                submit,
                UiFactory.row(login, about)
        );
        form.setMaxWidth(560);
        setContent("Register", authShell(authVisual(), form), false);
    }

    private void showDashboard() {
        if (!session.isSignedIn()) {
            showLogin();
            return;
        }
        AppUser user = session.requireUser();
        List<StudyGroup> joined = groupService.joinedGroups(user.getId());
        List<Message> messages = messageService.recentForUser(user.getId());
        List<Notification> notifications = notificationService.forUser(user.getId());

        HBox metrics = new HBox(14,
                metric("Joined groups", String.valueOf(joined.size())),
                metric("Recent messages", String.valueOf(messages.size())),
                metric("Unread notifications", String.valueOf(notificationService.unreadCount(user.getId())))
        );
        metrics.setFillHeight(true);

        FlowPane groupCards = cardFlow();
        joined.forEach(group -> groupCards.getChildren().add(groupCard(group)));
        VBox activity = UiFactory.card(UiFactory.sectionTitle("Recent messages"));
        messages.stream().limit(6).forEach(message -> activity.getChildren().add(messageRow(message)));
        if (messages.isEmpty()) {
            activity.getChildren().add(UiFactory.muted("No messages yet."));
        }

        VBox notices = UiFactory.card(UiFactory.sectionTitle("Notifications"));
        notifications.stream().limit(5).forEach(notification -> notices.getChildren().add(notificationRow(notification)));
        if (notifications.isEmpty()) {
            notices.getChildren().add(UiFactory.muted("No notifications yet."));
        }

        HBox lower = new HBox(16, activity, notices);
        HBox.setHgrow(activity, Priority.ALWAYS);
        HBox.setHgrow(notices, Priority.ALWAYS);
        VBox content = new VBox(20,
                UiFactory.badge("Dashboard"),
                UiFactory.title("Hello, " + user.getFullName()),
                metrics,
                UiFactory.sectionTitle("Your groups"),
                groupCards,
                lower
        );
        setContent("Dashboard", content, true);
    }

    private void showGroups() {
        TextField search = UiFactory.textField("Search finance, BI, marketing, comptabilite...");
        search.setPrefWidth(360);
        ComboBox<Category> category = categoryComboBox(false);
        category.setPrefWidth(360);
        Button filter = UiFactory.primaryButton("Filter");
        FlowPane cards = cardFlow();
        Runnable refresh = () -> {
            cards.getChildren().clear();
            Long categoryId = category.getValue() == null ? null : category.getValue().getId();
            groupService.search(search.getText(), categoryId).forEach(group -> cards.getChildren().add(groupCard(group)));
            if (cards.getChildren().isEmpty()) {
                cards.getChildren().add(UiFactory.card(UiFactory.muted("No groups matched your search.")));
            }
        };
        filter.setOnAction(event -> refresh.run());
        Button create = UiFactory.secondaryButton("Create group");
        create.setOnAction(event -> showGroupForm());
        refresh.run();
        VBox content = new VBox(18,
                UiFactory.greenBadge("Browse IHEC programs"),
                UiFactory.title("Explore Groups"),
                UiFactory.muted("Search by licence, master, specialization or subject, then join the right discussion room."),
                programOverview(),
                UiFactory.card(UiFactory.row(search, category, filter, create)),
                cards
        );
        setContent("Explore Groups", content, true);
    }

    private void showGroupDetails(Long groupId) {
        StudyGroup group = groupService.findDetailed(groupId);
        boolean member = session.currentUser().map(group::hasMember).orElse(false);
        boolean owner = session.currentUser().map(group::isOwnedBy).orElse(false);
        Button joinLeave = UiFactory.primaryButton(member ? "Leave group" : "Join group");
        joinLeave.setOnAction(event -> {
            if (!session.isSignedIn()) {
                showLogin();
                return;
            }
            runSafely("Membership", () -> {
                if (member) {
                    groupService.leaveGroup(groupId, session.requireUser());
                } else {
                    groupService.joinGroup(groupId, session.requireUser());
                }
                showGroupDetails(groupId);
            });
        });
        Button chat = UiFactory.secondaryButton("Open chat");
        chat.setOnAction(event -> showChat(groupId));
        Button share = UiFactory.secondaryButton("Share resource");
        share.setOnAction(event -> showResourceDialog(groupId));
        Button edit = UiFactory.secondaryButton("Edit");
        edit.setOnAction(event -> showGroupForm(group));

        VBox header = UiFactory.card(
                UiFactory.badge(group.getCategory().getName()),
                UiFactory.title(group.getName()),
                UiFactory.muted(group.getDescription()),
                UiFactory.row(joinLeave, chat, share, owner ? edit : new HBox()),
                UiFactory.row(metric("Matiere", nullToEmpty(group.getSubject())), metric("Rythme", group.getRhythm().getLabel()), metric("Format", group.getFormat().name()), metric("Members", group.getActiveMemberCount() + "/" + group.getCapacity()), metric("Price", group.priceLabel()))
        );

        VBox resources = UiFactory.card(UiFactory.sectionTitle("Resources"));
        for (LearningResource resource : resourceService.groupResources(groupId)) {
            resources.getChildren().add(resourceRow(resource));
        }
        if (resources.getChildren().size() == 1) {
            resources.getChildren().add(UiFactory.muted("No resources shared yet."));
        }

        VBox messages = UiFactory.card(UiFactory.sectionTitle("Discussion preview"));
        messageService.groupMessages(groupId).stream().limit(5).forEach(message -> messages.getChildren().add(messageRow(message)));
        if (messages.getChildren().size() == 1) {
            messages.getChildren().add(UiFactory.muted("No messages yet."));
        }

        HBox lower = new HBox(16, messages, resources);
        HBox.setHgrow(messages, Priority.ALWAYS);
        HBox.setHgrow(resources, Priority.ALWAYS);
        setContent(group.getName(), new VBox(18, header, lower), true);
    }

    private void showGroupForm() {
        if (!session.isSignedIn()) {
            showLogin();
            return;
        }
        showGroupForm(null);
    }

    private void showGroupForm(StudyGroup group) {
        TextField name = UiFactory.textField("Group name");
        TextArea description = UiFactory.textArea("Description");
        ComboBox<Category> category = categoryComboBox(true);
        category.setPrefWidth(360);
        ComboBox<String> subject = new ComboBox<>();
        subject.getStyleClass().add("field");
        subject.setPrefWidth(360);
        ComboBox<StudyFormat> format = new ComboBox<>(FXCollections.observableArrayList(StudyFormat.values()));
        format.getStyleClass().add("field");
        format.setPrefWidth(220);
        ComboBox<StudyRhythm> rhythm = new ComboBox<>(FXCollections.observableArrayList(StudyRhythm.values()));
        rhythm.getStyleClass().add("field");
        rhythm.setPrefWidth(220);
        rhythm.setConverter(new StringConverter<>() {
            @Override
            public String toString(StudyRhythm value) {
                return value == null ? "" : value.getLabel();
            }

            @Override
            public StudyRhythm fromString(String value) {
                return null;
            }
        });
        ComboBox<GroupVisibility> visibility = new ComboBox<>(FXCollections.observableArrayList(GroupVisibility.values()));
        visibility.getStyleClass().add("field");
        visibility.setPrefWidth(220);
        TextField capacity = UiFactory.textField("Capacity");
        TextField meetingLink = UiFactory.textField("Zoom / Meet link");
        TextField location = UiFactory.textField("Local location");
        CheckBox free = new CheckBox("Free group");
        TextField price = UiFactory.textField("Price");

        format.setValue(StudyFormat.HYBRID);
        rhythm.setValue(StudyRhythm.WEEKLY);
        visibility.setValue(GroupVisibility.PUBLIC);
        capacity.setText("20");
        free.setSelected(true);
        price.setDisable(true);
        free.selectedProperty().addListener((obs, oldValue, selected) -> price.setDisable(selected));
        updateSubjectOptions(category.getValue(), subject);
        category.valueProperty().addListener((obs, oldValue, selected) -> updateSubjectOptions(selected, subject));
        format.valueProperty().addListener((obs, oldValue, selected) -> syncLocationFields(selected, meetingLink, location));
        syncLocationFields(format.getValue(), meetingLink, location);

        if (group != null) {
            name.setText(group.getName());
            description.setText(group.getDescription());
            category.setValue(group.getCategory());
            updateSubjectOptions(group.getCategory(), subject);
            subject.setValue(group.getSubject());
            format.setValue(group.getFormat());
            rhythm.setValue(group.getRhythm());
            visibility.setValue(group.getVisibility());
            capacity.setText(String.valueOf(group.getCapacity()));
            meetingLink.setText(group.getMeetingLink());
            location.setText(group.getLocation());
            free.setSelected(group.isFree());
            price.setDisable(group.isFree());
            price.setText(group.getPrice() == null ? "0" : group.getPrice().toString());
        }

        Button save = UiFactory.primaryButton("Save group");
        save.setOnAction(event -> runSafely("Save group", () -> {
            if (category.getValue() == null) {
                throw new IllegalArgumentException("Please choose a level/program.");
            }
            if (subject.getValue() == null || subject.getValue().isBlank()) {
                throw new IllegalArgumentException("Please choose a matiere.");
            }
            GroupForm form = new GroupForm();
            form.setName(name.getText());
            form.setDescription(description.getText());
            form.setCategoryId(category.getValue().getId());
            form.setSubject(subject.getValue());
            form.setFormat(format.getValue());
            form.setRhythm(rhythm.getValue());
            form.setVisibility(visibility.getValue());
            form.setCapacity(Integer.parseInt(capacity.getText()));
            form.setMeetingLink(meetingLink.getText());
            form.setLocation(location.getText());
            form.setFree(free.isSelected());
            form.setPrice(free.isSelected() ? BigDecimal.ZERO : new BigDecimal(price.getText()));
            StudyGroup saved = group == null
                    ? groupService.createGroup(form, session.requireUser())
                    : groupService.updateGroup(group.getId(), form, session.requireUser());
            showGroupDetails(saved.getId());
        }));

        VBox academic = UiFactory.card(
                UiFactory.greenBadge("Academic"),
                UiFactory.sectionTitle("Niveau and matiere"),
                UiFactory.muted("Choose the IHEC program first; the matiere list adapts automatically."),
                field("Niveau / programme", category),
                field("Matiere", subject)
        );
        VBox logistics = UiFactory.card(
                UiFactory.goldBadge("Session"),
                UiFactory.sectionTitle("Rhythm and location"),
                UiFactory.row(field("Format", format), field("Rythme", rhythm), field("Visibility", visibility)),
                UiFactory.row(field("Zoom link", meetingLink), field("Local room", location)),
                field("Capacity", capacity)
        );
        VBox basics = UiFactory.card(
                UiFactory.badge(group == null ? "New group" : "Edit group"),
                UiFactory.title(group == null ? "Create Group" : "Edit Group"),
                field("Group name", name),
                field("Description", description)
        );
        VBox pricing = UiFactory.card(
                UiFactory.greenBadge("Access"),
                UiFactory.sectionTitle("Price"),
                free,
                field("Price", price),
                save
        );
        HBox mainRow = new HBox(18, basics, academic);
        HBox.setHgrow(basics, Priority.ALWAYS);
        HBox.setHgrow(academic, Priority.ALWAYS);
        HBox secondRow = new HBox(18, logistics, pricing);
        HBox.setHgrow(logistics, Priority.ALWAYS);
        HBox.setHgrow(pricing, Priority.ALWAYS);
        VBox panel = new VBox(18,
                UiFactory.greenBadge("Create a polished IHEC group"),
                UiFactory.title(group == null ? "Create Group" : "Edit Group"),
                UiFactory.muted("Complete the academic context, session rhythm, and place. Online sessions use Zoom/Meet; in-person sessions use a local room."),
                mainRow,
                secondRow
        );
        setContent("Group Form", panel, true);
    }

    private void showChat(Long groupId) {
        if (!session.isSignedIn()) {
            showLogin();
            return;
        }
        StudyGroup group = groupService.findDetailed(groupId);
        VBox messages = new VBox(12);
        messages.getStyleClass().add("list-pane");
        messageService.groupMessages(groupId).forEach(message -> messages.getChildren().add(messageRow(message)));
        if (messages.getChildren().isEmpty()) {
            messages.getChildren().add(UiFactory.muted("Start the first discussion."));
        }
        TextArea input = UiFactory.textArea("Type your message...");
        input.setPrefRowCount(3);
        Button send = UiFactory.primaryButton("Send");
        send.setOnAction(event -> runSafely("Send message", () -> {
            messageService.sendMessage(groupId, session.requireUser(), input.getText());
            showChat(groupId);
        }));
        VBox content = UiFactory.card(
                UiFactory.badge(group.getCategory().getName()),
                UiFactory.title(group.getName() + " Chat"),
                messages,
                input,
                send
        );
        setContent("Chat", content, true);
    }

    private void showProfile() {
        if (!session.isSignedIn()) {
            showLogin();
            return;
        }
        AppUser user = userService.findById(session.requireUser().getId());
        FlowPane groups = cardFlow();
        groupService.joinedGroups(user.getId()).forEach(group -> groups.getChildren().add(groupCard(group)));
        VBox profile = UiFactory.card(
                UiFactory.badge(user.getRole().name()),
                UiFactory.title(user.getFullName()),
                UiFactory.muted(user.getEmail()),
                UiFactory.muted(nullToEmpty(user.getUniversity()) + "  " + nullToEmpty(user.getMajor())),
                UiFactory.muted(nullToEmpty(user.getBio()))
        );
        setContent("Profile", new VBox(18, profile, UiFactory.sectionTitle("Joined groups"), groups), true);
    }

    private void showSettings() {
        if (!session.isSignedIn()) {
            showLogin();
            return;
        }
        AppUser user = session.requireUser();
        TextField name = UiFactory.textField("Full name");
        TextField university = UiFactory.textField("University");
        TextField major = UiFactory.textField("Major");
        TextField avatar = UiFactory.textField("Avatar URL");
        TextArea bio = UiFactory.textArea("Bio");
        name.setText(user.getFullName());
        university.setText(nullToEmpty(user.getUniversity()));
        major.setText(nullToEmpty(user.getMajor()));
        avatar.setText(nullToEmpty(user.getAvatarUrl()));
        bio.setText(nullToEmpty(user.getBio()));
        Button save = UiFactory.primaryButton("Save profile");
        save.setOnAction(event -> runSafely("Save profile", () -> {
            ProfileForm form = new ProfileForm();
            form.setFullName(name.getText());
            form.setUniversity(university.getText());
            form.setMajor(major.getText());
            form.setAvatarUrl(avatar.getText());
            form.setBio(bio.getText());
            session.signIn(userService.updateProfile(user.getId(), form));
            showProfile();
        }));
        VBox panel = UiFactory.card(
                UiFactory.badge("Settings"),
                UiFactory.title("Edit Profile"),
                field("Full name", name),
                field("University", university),
                field("Major", major),
                field("Avatar URL", avatar),
                field("Bio", bio),
                save
        );
        panel.setMaxWidth(720);
        setContent("Settings", panel, true);
    }

    private void showNotifications() {
        if (!session.isSignedIn()) {
            showLogin();
            return;
        }
        VBox list = UiFactory.card(UiFactory.sectionTitle("Notifications"));
        notificationService.forUser(session.requireUser().getId()).forEach(notification -> list.getChildren().add(notificationRow(notification)));
        if (list.getChildren().size() == 1) {
            list.getChildren().add(UiFactory.muted("No notifications yet."));
        }
        setContent("Notifications", new VBox(18, UiFactory.badge("Activity center"), UiFactory.title("Notifications"), list), true);
    }

    private void showSearch() {
        TextField query = UiFactory.textField("Search students or groups");
        FlowPane groups = cardFlow();
        VBox users = UiFactory.card(UiFactory.sectionTitle("Students"));
        Button submit = UiFactory.primaryButton("Search");
        Runnable refresh = () -> {
            groups.getChildren().clear();
            users.getChildren().setAll(UiFactory.sectionTitle("Students"));
            groupService.search(query.getText(), null).forEach(group -> groups.getChildren().add(groupCard(group)));
            userService.search(query.getText()).forEach(user -> users.getChildren().add(userRow(user)));
        };
        submit.setOnAction(event -> refresh.run());
        refresh.run();
        setContent("Search", new VBox(18,
                UiFactory.badge("Search"),
                UiFactory.title("Find People and Groups"),
                UiFactory.card(UiFactory.row(query, submit)),
                UiFactory.sectionTitle("Groups"),
                groups,
                users
        ), true);
    }

    private void showAbout() {
        VBox oop = UiFactory.card(UiFactory.sectionTitle("OOP"), UiFactory.muted("Encapsulation, inheritance, abstraction, polymorphism, interfaces, abstract classes, overriding, overloading, and layered responsibilities."));
        VBox architecture = UiFactory.card(UiFactory.sectionTitle("Architecture"), UiFactory.muted("Spring Boot services, repositories, JPA entities, DTOs, MySQL relationships, and JavaFX desktop views."));
        VBox features = UiFactory.card(UiFactory.sectionTitle("IHEC Data"), UiFactory.muted("Programmes for Licence Sciences de Gestion, Licence Informatique de Gestion - BI, and masters in finance, marketing, management, comptabilite, entrepreneuriat and big data."));
        HBox cards = new HBox(16, oop, architecture, features);
        HBox.setHgrow(oop, Priority.ALWAYS);
        HBox.setHgrow(architecture, Priority.ALWAYS);
        HBox.setHgrow(features, Priority.ALWAYS);
        VBox content = new VBox(18, UiFactory.badge("University project"), UiFactory.title("About StudyGroup"), UiFactory.muted("A polished desktop application for collaborative study groups at IHEC."), cards);
        if (!session.isSignedIn()) {
            Button login = UiFactory.primaryButton("Login");
            login.setOnAction(event -> showLogin());
            Button register = UiFactory.secondaryButton("Register");
            register.setOnAction(event -> showRegister());
            content.getChildren().add(UiFactory.row(login, register));
        }
        setContent("About", content, session.isSignedIn());
    }

    private VBox groupCard(StudyGroup group) {
        Button open = UiFactory.secondaryButton("Open");
        open.setOnAction(event -> showGroupDetails(group.getId()));
        Button chat = UiFactory.secondaryButton("Chat");
        chat.setOnAction(event -> showChat(group.getId()));
        VBox card = UiFactory.softCard(
                UiFactory.row(UiFactory.greenBadge(levelLabel(group.getCategory())), UiFactory.goldBadge(group.getFormat().name())),
                UiFactory.sectionTitle(group.getName()),
                UiFactory.muted(group.getCategory().getName() + " / " + nullToEmpty(group.getSubject())),
                UiFactory.muted(group.getDescription()),
                UiFactory.row(UiFactory.badge(group.priceLabel()), UiFactory.muted(group.getRhythm().getLabel()), UiFactory.muted(group.getActiveMemberCount() + "/" + group.getCapacity() + " members")),
                UiFactory.row(open, chat)
        );
        card.getStyleClass().add("group-card");
        card.setPrefWidth(300);
        return card;
    }

    private Node messageRow(Message message) {
        Label author = new Label(message.getSender().getDisplayName());
        author.getStyleClass().add("section-title");
        return UiFactory.card(author, UiFactory.muted(message.getContent()));
    }

    private Node resourceRow(LearningResource resource) {
        return UiFactory.card(
                UiFactory.badge(resource.getResourceType().name()),
                UiFactory.sectionTitle(resource.getTitle()),
                UiFactory.muted(nullToEmpty(resource.getDescription())),
                UiFactory.muted(resource.getAccessLabel())
        );
    }

    private Node notificationRow(Notification notification) {
        Button read = UiFactory.secondaryButton(notification.isReadStatus() ? "Read" : "Mark read");
        read.setDisable(notification.isReadStatus());
        read.setOnAction(event -> {
            notificationService.markRead(notification.getId());
            showNotifications();
        });
        return UiFactory.card(
                UiFactory.badge(notification.getType().name()),
                UiFactory.sectionTitle(notification.getTitle()),
                UiFactory.muted(notification.getBody()),
                read
        );
    }

    private Node userRow(AppUser user) {
        return UiFactory.card(
                UiFactory.badge(user.getInitials()),
                UiFactory.sectionTitle(user.getFullName()),
                UiFactory.muted(nullToEmpty(user.getMajor())),
                UiFactory.muted(user.getEmail())
        );
    }

    private VBox metric(String label, String value) {
        VBox metric = UiFactory.card(UiFactory.muted(label), UiFactory.title(value));
        metric.setPrefWidth(240);
        HBox.setHgrow(metric, Priority.ALWAYS);
        return metric;
    }

    private FlowPane cardFlow() {
        FlowPane flow = new FlowPane(16, 16);
        flow.setPrefWrapLength(900);
        return flow;
    }

    private FlowPane programOverview() {
        FlowPane flow = new FlowPane(12, 12);
        flow.getChildren().add(UiFactory.programChip(UiFactory.greenBadge("Licence"), UiFactory.smallTitle("Sciences de Gestion"), UiFactory.muted("Gestion, Comptabilite, Finance, Marketing, Management")));
        flow.getChildren().add(UiFactory.programChip(UiFactory.greenBadge("Licence"), UiFactory.smallTitle("Informatique de Gestion"), UiFactory.muted("Business Intelligence, SQL, data warehouse")));
        flow.getChildren().add(UiFactory.programChip(UiFactory.goldBadge("Master"), UiFactory.smallTitle("Finance & Analyste Financier"), UiFactory.muted("Valorisation, portefeuille, diagnostic")));
        flow.getChildren().add(UiFactory.programChip(UiFactory.goldBadge("Master"), UiFactory.smallTitle("Big Data, Marketing, Management"), UiFactory.muted("Analytics, e-commerce, strategy, CRM")));
        flow.getChildren().add(UiFactory.programChip(UiFactory.goldBadge("Master"), UiFactory.smallTitle("Comptabilite & Entrepreneuriat"), UiFactory.muted("Audit, IFRS, business plan, innovation")));
        return flow;
    }

    private void updateSubjectOptions(Category category, ComboBox<String> subjectComboBox) {
        String current = subjectComboBox.getValue();
        subjectComboBox.setItems(FXCollections.observableArrayList(subjectsFor(category)));
        if (current != null && subjectComboBox.getItems().contains(current)) {
            subjectComboBox.setValue(current);
        } else if (!subjectComboBox.getItems().isEmpty()) {
            subjectComboBox.setValue(subjectComboBox.getItems().get(0));
        }
    }

    private List<String> subjectsFor(Category category) {
        if (category == null || category.getName() == null) {
            return List.of();
        }
        String name = category.getName();
        if (name.contains("Tronc Commun")) {
            return List.of("Comptabilite generale", "Microeconomie", "Statistiques", "Droit des affaires", "Introduction au management");
        }
        if (name.contains("2eme annee - Sciences")) {
            return List.of("Methodes quantitatives", "Marketing fondamental", "Gestion financiere", "Management des organisations", "Controle de gestion");
        }
        if (name.contains("2eme annee - Comptabilite")) {
            return List.of("Comptabilite approfondie", "Fiscalite", "Droit comptable", "Controle interne", "Audit introduction");
        }
        if (name.contains("3eme annee - Finance")) {
            return List.of("Analyse financiere", "Gestion de portefeuille", "Marches financiers", "Finance d'entreprise", "Mathematiques financieres");
        }
        if (name.contains("3eme annee - Marketing")) {
            return List.of("Etudes de marche", "Marketing strategique", "Comportement consommateur", "Marketing digital", "Communication commerciale");
        }
        if (name.contains("3eme annee - Management")) {
            return List.of("Management strategique", "Gestion RH", "Entrepreneuriat", "Leadership", "Theorie des organisations");
        }
        if (name.contains("Informatique Gestion")) {
            return List.of("Base de donnees", "SQL avance", "Data warehouse", "Business Intelligence", "Programmation Java", "Systemes d'information");
        }
        if (name.contains("Entrepreneuriat")) {
            return List.of("Business plan", "Innovation", "Pitch deck", "Financement startup", "Creation d'entreprise");
        }
        if (name.contains("Analyste Financier")) {
            return List.of("Valorisation d'entreprise", "Diagnostic financier", "Gestion du risque", "Reporting financier", "Modelisation Excel");
        }
        if (name.contains("Big Data")) {
            return List.of("Data analytics", "Machine learning", "E-commerce analytics", "Python data", "Big data architecture");
        }
        if (name.contains("Marketing")) {
            return List.of("Marketing digital", "CRM", "Strategie de marque", "Recherche marketing", "Consumer insight");
        }
        if (name.contains("Comptabilite")) {
            return List.of("Audit et IFRS", "Consolidation", "Fiscalite approfondie", "Controle de gestion", "Comptabilite internationale");
        }
        if (name.contains("Management")) {
            return List.of("Strategie d'entreprise", "Gouvernance", "Conduite du changement", "Management international", "Leadership");
        }
        if (name.contains("Finance")) {
            return List.of("Gestion de portefeuille", "Finance de marche", "Finance d'entreprise", "Derives et risque", "Investissement");
        }
        return List.of("Revision generale", "Travaux diriges", "Projet de groupe");
    }

    private void syncLocationFields(StudyFormat format, TextField meetingLink, TextField location) {
        StudyFormat selected = format == null ? StudyFormat.HYBRID : format;
        boolean onlineOnly = selected == StudyFormat.ONLINE;
        boolean inPersonOnly = selected == StudyFormat.IN_PERSON;
        meetingLink.setDisable(inPersonOnly);
        location.setDisable(onlineOnly);
        if (inPersonOnly) {
            meetingLink.clear();
            meetingLink.setPromptText("Disabled for in-person");
            location.setPromptText("Room / campus location");
        } else if (onlineOnly) {
            location.clear();
            location.setPromptText("Disabled for online");
            meetingLink.setPromptText("Zoom / Meet link");
        } else {
            meetingLink.setPromptText("Zoom / Meet link");
            location.setPromptText("Room / campus location");
        }
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        return grid;
    }

    private VBox field(String label, Node input) {
        Label fieldLabel = new Label(label);
        fieldLabel.setStyle("-fx-font-weight: 800;");
        VBox box = new VBox(7, fieldLabel, input);
        return box;
    }

    private VBox centered(Node node) {
        VBox center = new VBox(node);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(30));
        return center;
    }

    private HBox authShell(Node visual, Node form) {
        HBox shell = new HBox(22, visual, form);
        shell.setAlignment(Pos.CENTER);
        HBox.setHgrow(visual, Priority.ALWAYS);
        HBox.setHgrow(form, Priority.ALWAYS);
        return shell;
    }

    private VBox authVisual() {
        Label title = new Label("IHEC Study Hub");
        title.getStyleClass().add("visual-title");
        Label text = new Label("A desktop workspace for licences, masters, BI, finance, marketing, management, comptabilite and entrepreneurship groups.");
        text.getStyleClass().add("visual-text");
        text.setWrapText(true);

        VBox stats = new VBox(12,
                visualStat("2", "Licences"),
                visualStat("7", "Masters"),
                visualStat("14+", "Ready groups"),
                visualStat("MySQL", "Persistent database")
        );
        VBox panel = new VBox(20,
                UiFactory.greenBadge("Modern JavaFX desktop"),
                title,
                text,
                stats
        );
        panel.getStyleClass().add("visual-panel");
        panel.setPrefWidth(500);
        panel.setMinHeight(520);
        return panel;
    }

    private VBox visualStat(String value, String label) {
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("visual-title");
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("visual-text");
        VBox box = new VBox(3, valueLabel, labelNode);
        box.getStyleClass().add("visual-card");
        return box;
    }

    private ComboBox<Category> categoryComboBox(boolean required) {
        ComboBox<Category> comboBox = new ComboBox<>(FXCollections.observableArrayList(categoryRepository.findAll()));
        comboBox.setPromptText("All categories");
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                if (category == null) {
                    return "All categories";
                }
                String level = category.getAcademicLevel() == null || category.getAcademicLevel().isBlank()
                        ? ""
                        : category.getAcademicLevel() + " - ";
                return level + category.getName();
            }

            @Override
            public Category fromString(String value) {
                return null;
            }
        });
        comboBox.getStyleClass().add("field");
        if (required && !comboBox.getItems().isEmpty()) {
            comboBox.setValue(comboBox.getItems().get(0));
        }
        return comboBox;
    }

    private void showResourceDialog(Long groupId) {
        if (!session.isSignedIn()) {
            showLogin();
            return;
        }
        Dialog<ResourceForm> dialog = new Dialog<>();
        dialog.setTitle("Share resource");
        TextField title = UiFactory.textField("Title");
        TextArea description = UiFactory.textArea("Description");
        ComboBox<ResourceType> type = new ComboBox<>(FXCollections.observableArrayList(ResourceType.values()));
        type.setValue(ResourceType.LINK);
        TextField url = UiFactory.textField("URL");
        TextField fileName = UiFactory.textField("File name");
        TextField path = UiFactory.textField("Local path or note");
        VBox body = new VBox(12, field("Type", type), field("Title", title), field("Description", description), field("URL", url), field("File name", fileName), field("Local path", path));
        body.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(body);
        dialog.getDialogPane().getButtonTypes().addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType != javafx.scene.control.ButtonType.OK) {
                return null;
            }
            ResourceForm form = new ResourceForm();
            form.setResourceType(type.getValue());
            form.setTitle(title.getText());
            form.setDescription(description.getText());
            form.setUrl(url.getText());
            form.setFileName(fileName.getText());
            form.setStoragePath(path.getText());
            return form;
        });
        dialog.showAndWait().ifPresent(form -> runSafely("Share resource", () -> {
            resourceService.share(groupId, form, session.requireUser());
            showGroupDetails(groupId);
        }));
    }

    private void runSafely(String title, Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException exception) {
            showError(title, exception.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null ? "Unexpected error." : message);
        alert.showAndWait();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String levelLabel(Category category) {
        return category.getAcademicLevel() == null || category.getAcademicLevel().isBlank()
                ? "Subject"
                : category.getAcademicLevel();
    }
}
