
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.apache.mina.core.future.ReadFuture;

import javax.swing.*;

public class ChatWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea textAreaField;

    @FXML
    private TextField userInputField;

    @FXML
    private TextFlow listOfUsersField;

    @FXML
    private Button exitButton;

    @FXML
    private Button addUserButton;

    @FXML
    private Button sendButton;

    private boolean registrationFormOpened;
    private static ChatWindowController instance;
    private Map<String, Boolean> userList = new TreeMap<>();

    private ChatWindowController() {
        registrationFormOpened = false;
    }

    public static ChatWindowController getInstance() {
        if (instance == null) {
            synchronized (ChatWindowController.class) {
                if (instance == null) {
                    instance = new ChatWindowController();
                }
            }
        }
        return instance;
    }

    public void setRegistrationFormClosed() {
        registrationFormOpened = false;
    }


    @FXML
    void initialize() {
        System.out.println("Chat window controller started");
        ConnectionManager.getInstance().getSession().write(JsonMessageHandler.createGetMessageHistoryString());
        exitButton.setOnAction(event -> {
            ConnectionManager.getInstance().DisconnectFromServer();
            exitButton.getScene().getWindow().hide();
            System.exit(0);
        });
        sendButton.setOnAction(event -> {
            String localMessage = userInputField.getText().trim();
            checkAndSendMessage(localMessage);
        });
        userInputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String localMessage = userInputField.getText().trim();
                checkAndSendMessage(localMessage);
            }
        });
        if (ConnectionManager.getInstance().getSession().getAttribute("login").equals("admin"))
            addUserButton.setDisable(false);
        addUserButton.setOnAction(event -> {
            try {
                showRegistrationForm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public synchronized void addReceivedMessageToChat(ChatMessage chatMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append(chatMessage.getSender());
        sb.append(" [");
        sb.append(chatMessage.getTime());
        sb.append("]:\n");
        sb.append(chatMessage.getMessage());
        sb.append("\n\n");
        Platform.runLater(() -> {
            textAreaField.appendText(sb.toString());
        });
    }

    public synchronized void addUserToUserList(User user) {
        String username = user.getUsername();
        boolean online = user.getStatus() == 1;
        userList.put(username, online);
        if (userList.containsKey(username)) {
            displayUsers();
        } else {
            displayUser(username);
        }
    }

    private void checkAndSendMessage(String message) {
        if (!message.isEmpty()) {
            Date date = new Date();
            SimpleDateFormat dateFormatToSend = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeToSend = dateFormatToSend.format(date);
            System.out.println(timeToSend);
            String time = dateFormat.format(date);
            ConnectionManager.getInstance().getSession().write(JsonMessageHandler.createMessageString(time, message));
            userInputField.setText("");
            textAreaField.appendText(ConnectionManager.getInstance().getSession().getAttribute("login") + " [" + timeToSend + "]:\n" + message + "\n\n");
        }
    }

    private void showRegistrationForm() throws IOException {
        if (!registrationFormOpened) {
            registrationFormOpened = true;
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegistrationForm.fxml"));
            fxmlLoader.setController(RegistrationFormController.getInstance());
            Parent root = null;
            root = fxmlLoader.load();
            stage.setTitle("Registration Form");
            stage.setScene(new Scene(root, 400, 200));
            stage.setOnCloseRequest(event -> {
                registrationFormOpened = false;
            });
            stage.showAndWait();
        } else {
            System.out.println("registration form already opened");
        }
    }

    private synchronized void displayUsers() {
        javafx.application.Platform.runLater(() -> {
            listOfUsersField.getChildren().clear();
        });
        for (String entry : userList.keySet()) {
            displayUser(entry);
        }
    }

    private synchronized void displayUser(String username) {
        Text t = new Text(username + "\n");
        if (userList.get(username)) {
            t.setFill(Color.GREEN);
        } else {
            t.setFill(Color.RED);
        }
        javafx.application.Platform.runLater(() -> {
            listOfUsersField.getChildren().add(t);
        });
    }
}

