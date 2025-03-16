package com.example.javachatclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ChatController extends Controller {
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    @FXML
    private TextField sendMessageContent;
    @FXML
    private Button sendMessageButton;
    @FXML
    private VBox vboxMessages;

    public ChatController(Main main) {
        super(main, "chat-view.fxml");

        try {
            socket = new Socket("localhost",17777);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject connectRequest = new JSONObject();
        connectRequest.put("id", main.getUserId().toString());
        connectRequest.put("action", "connect");
        try {
            bw.write(connectRequest.toString());
            bw.newLine();
            bw.flush();
            String response = br.readLine();
            JSONObject responseJson = new JSONObject(response);
            if (!responseJson.getBoolean("success")) {
                throw new RuntimeException("Connection failed");
            }
            JSONArray messages = responseJson.getJSONArray("messages");
            for (int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                displayNewMessage(message.getString("message"), message.getString("senderName"), UUID.fromString(message.getString("senderId")), message.getString("sentAt"));
            }
            System.out.println("Connected to chat successfully");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String newMessage;
                    while (socket.isConnected()) {
                        try {
                            newMessage = br.readLine();
                            System.out.println(newMessage);
                            JSONObject responseJson = new JSONObject(newMessage);
                            if (responseJson.getString("action").equals("newMessage") && responseJson.getBoolean("success")) {
                                Platform.runLater(() -> {
                                    displayNewMessage(responseJson.getString("message"), responseJson.getString("senderName"), UUID.fromString(responseJson.getString("senderId")), responseJson.getString("sentAt"));
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessageSend() {
        if (!sendMessageContent.getText().isEmpty()) {
            JSONObject messageRequest = new JSONObject();
            messageRequest.put("id", main.getUserId().toString());
            messageRequest.put("message", sendMessageContent.getText());
            messageRequest.put("action", "sendMessage");
            try {
                bw.write(messageRequest.toString());
                bw.newLine();
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendMessageContent.clear();
        }
    }

    private void displayNewMessage(String message, String sender, UUID senderId, String timestamp) {
        VBox vbox = new VBox();
        vbox.setSpacing(2);

        Label senderLabel = new Label(sender);
        senderLabel.setStyle("-fx-font-weight: bold;");

        HBox hbox = new HBox();
        hbox.setSpacing(5);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS");
        System.out.println(timestamp);
        Date date = null;
        try {
            date = parser.parse(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        Label timeLabel = new Label(formatter.format(date));
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        if (senderId.equals(main.getUserId())) {
            vbox.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.setStyle("-fx-background-color: #0078ff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 10;");
            senderLabel.setStyle("-fx-font-weight: bold; text-alignment: right;");
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray; text-alignment: right;");
            senderLabel.setText("You");
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            hbox.getChildren().add(region);
        } else {
            vbox.setAlignment(Pos.CENTER_LEFT);
            messageLabel.setStyle("-fx-background-color: #e1ffc7; -fx-padding: 10; -fx-background-radius: 10;");
        }
        hbox.getChildren().addAll(messageLabel);
        vbox.getChildren().addAll(senderLabel, hbox, timeLabel);
        vboxMessages.getChildren().add(vbox);
    }

    @FXML
    private void initialize() {
        sendMessageButton.setOnAction(event -> handleMessageSend());
    }
}
