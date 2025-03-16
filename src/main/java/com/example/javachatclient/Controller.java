package com.example.javachatclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

abstract public class Controller {
    protected final Main main;

    public Controller(Main main, String view) {
        this.main = main;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
            loader.setController(this);
            main.stage.setScene(new Scene(loader.load(), main.WindowWidth, main.WindowHeight));
            main.stage.setTitle(main.WindowTitle);
            main.stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
