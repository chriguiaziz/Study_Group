package com.studygroup.desktop;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class UiFactory {

    private UiFactory() {
    }

    public static Label title(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("title");
        return label;
    }

    public static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        label.setWrapText(true);
        return label;
    }

    public static Label smallTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("small-title");
        label.setWrapText(true);
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted");
        label.setWrapText(true);
        return label;
    }

    public static Label badge(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("badge");
        return label;
    }

    public static Label greenBadge(String text) {
        Label label = badge(text);
        label.getStyleClass().add("badge-green");
        return label;
    }

    public static Label goldBadge(String text) {
        Label label = badge(text);
        label.getStyleClass().add("badge-gold");
        return label;
    }

    public static Label avatar(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("avatar");
        return label;
    }

    public static Button primaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        return button;
    }

    public static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        return button;
    }

    public static Button dangerButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("danger-button");
        return button;
    }

    public static Button navButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    public static VBox card(Node... nodes) {
        VBox box = new VBox(12, nodes);
        box.getStyleClass().add("card");
        return box;
    }

    public static VBox softCard(Node... nodes) {
        VBox box = new VBox(10, nodes);
        box.getStyleClass().add("soft-card");
        return box;
    }

    public static VBox programChip(Node... nodes) {
        VBox box = new VBox(6, nodes);
        box.getStyleClass().add("program-chip");
        return box;
    }

    public static TextField textField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("field");
        return field;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("field");
        return field;
    }

    public static TextArea textArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.getStyleClass().add("field");
        area.setWrapText(true);
        return area;
    }

    public static HBox row(Node... nodes) {
        HBox row = new HBox(10, nodes);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    public static Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public static ScrollPane scroll(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(Insets.EMPTY);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scrollPane;
    }
}
