package com.lumetix;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.lumetix.entity.BasicConstants.MainUi.*;
import static com.lumetix.ui.MainUI.newMainUI;

public class ApplicationLauncher extends Application {

    public static Stage primaryStage;

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ApplicationLauncher.primaryStage = primaryStage;
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        primaryStage.setTitle(TITLE + " " + VERSION);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        Scene scene = new Scene(newMainUI());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
