package com.example.javachatclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

public class WelcomeController extends Controller {
    @FXML
    private Button chooseConfigFileButton;
    @FXML
    private Button createNewUserButton;

    public WelcomeController(Main main) {
        super(main, "welcome-view.fxml");
    }

    private void openChooseConfigFileController() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open configuration file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialDirectory(new java.io.File("."));
        java.io.File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null && selectedFile.exists()) {
            try {
                InputStream inputStream = new FileInputStream(selectedFile);
                String data = new String(inputStream.readAllBytes());
                inputStream.close();
                JSONObject dataJson = new JSONObject(data);

                JSONObject userCheckRequest = new JSONObject();
                userCheckRequest.put("id", dataJson.getString("userId"));
                userCheckRequest.put("action", "checkUser");

                Socket socket = new Socket("localhost",17777);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(userCheckRequest.toString());
                bw.newLine();
                bw.flush();
                String response = br.readLine();
                socket.close();
                JSONObject responseJson = new JSONObject(response);
                if (responseJson.getBoolean("success")) {
                    main.setUserId(UUID.fromString(dataJson.getString("userId")));
                    new ChatController(main);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openNewUserController() {
        new NewUserController(main);
    }

    @FXML
    private void initialize() {
        chooseConfigFileButton.setOnAction(event -> openChooseConfigFileController());
        createNewUserButton.setOnAction(event -> openNewUserController());
    }

//    @FXML
//    public void handleLoadConfiguration(ActionEvent actionEvent) throws IOException {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open Resource File");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("JSON Files", "*.json")
//        );
//        fileChooser.setInitialDirectory(new java.io.File("."));
//        java.io.File selectedFile = fileChooser.showOpenDialog(null);
//        if (selectedFile != null) {
//
//        }
//    }
//
//    @FXML
//    public void switchToNewUserView(ActionEvent actionEvent) throws IOException {
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("new-user-view.fxml")));
//        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
//        Scene scene = new Scene(root, Main.WindowWidth, Main.WindowHeight);
//        stage.setScene(scene);
//        stage.setTitle(Main.WindowTitle);
//        stage.show();
//        stagee;
//    }
}
