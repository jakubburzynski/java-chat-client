package com.example.javachatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class Main extends Application {
    public final Integer WindowWidth = 1280;
    public final Integer WindowHeight = 720;
    public final String WindowTitle = "Java Chat Client";
    public final String ServerAddress = "localhost";
    public final Integer ServerPort = 17777;
    public final Stage stage = new Stage();
    private UUID userId;

    @Override
    public void start(Stage stage) throws IOException {
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("chat-view.fxml")));
//        Scene scene = new Scene(root, WindowWidth, WindowHeight);
//        stage.setScene(scene);
//        stage.setTitle(WindowTitle);
//        stage.show();
        WelcomeController welcomeController = new WelcomeController(this);

    }

    public static void main(String[] args) {
        launch();
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}