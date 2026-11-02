package com.studygroup.desktop;

import com.studygroup.StudyGroupApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class StudyGroupDesktopApplication extends Application {

    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        context = new SpringApplicationBuilder(StudyGroupApplication.class)
                .web(WebApplicationType.NONE)
                .run();
    }

    @Override
    public void start(Stage stage) {
        DesktopNavigator navigator = context.getBean(DesktopNavigator.class);
        navigator.attach(stage);
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
