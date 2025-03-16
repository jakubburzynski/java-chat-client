package com.example.javachatclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

public class NewUserController extends Controller {
    @FXML
    private TextField username;
    @FXML
    private Button createNewUserButton;

    public NewUserController(Main main) {
        super(main, "new-user-view.fxml");
    }

    private void handleNewUserCreation() throws IOException {
        if (username.getText().isEmpty()) {
            return;
        }
        JSONObject userCreateRequest = new JSONObject();
        userCreateRequest.put("username", username.getText());
        userCreateRequest.put("action", "createUser");

        Socket socket = new Socket("localhost",17777);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write(userCreateRequest.toString());
        bw.newLine();
        bw.flush();
        String response = br.readLine();
        socket.close();
        JSONObject responseJson = new JSONObject(response);
        if (responseJson.getBoolean("success")) {
            JSONObject storedData = new JSONObject();
            storedData.put("userId", responseJson.getString("id"));
            storedData.put("userName", username.getText());
            try {
                FileWriter fileWriter = new FileWriter("data_" + username.getText() + ".json");
                fileWriter.write(storedData.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            main.setUserId(UUID.fromString(responseJson.getString("id")));
            System.out.println("User created successfully");
            new ChatController(main);
        } else {
            System.out.println("User creation failed");
        }
    }

    @FXML
    private void initialize() {
        createNewUserButton.setOnAction(event -> {
            try {
                handleNewUserCreation();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
