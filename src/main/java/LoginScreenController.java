
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.mina.core.session.IoSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginScreenController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordText;

    @FXML
    private TextField loginText;

    @FXML
    private Button cancelButton;

    private static LoginScreenController instance;

    private LoginScreenController() {

    }

    public static LoginScreenController getInstance() {
        if (instance == null) {
            synchronized (LoginScreenController.class) {
                if (instance == null) {
                    instance = new LoginScreenController();
                }
            }
        }
        return instance;
    }

    @FXML
    void initialize() {
        ConnectionManager cm = ConnectionManager.getInstance();
        if (cm.getSession() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Error Connecting to server");
            alert.setContentText("Make sure that server is running");
            alert.showAndWait();
            javafx.application.Platform.exit();
            System.exit(0);
        }
        cancelButton.setOnAction(event -> {
            System.out.println("cancel pressed");
            cancelButton.getScene().getWindow().hide();
            Platform.exit();
            System.exit(0);
        });
        passwordText.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginButton.fire();
            }
        });
        loginButton.setOnAction(event -> {
            String loginString = loginText.getText().trim();
            String passwordString = passwordText.getText().trim();
            if (checkField(loginString) && checkField(passwordString)) {
                //send login request to server
                if (cm.getSession() == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Error connecting to server");
                    alert.setContentText("Make sure that server is running and try again");
                    alert.showAndWait();
                    cm.StartConnection();
                } else {
                    cm.getSession().write(JsonMessageHandler.createLoginString(loginString, passwordString));
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Your name or password is not specified or contains blank spaces!");
                alert.setHeaderText("Error in Fields");
                alert.showAndWait();
            }
        });

    }

    public void errorWhileLoggingIn(String s) {
        System.out.println("errorwhileloggin in");
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Error while logging in");
            alert.setContentText("message from server: " + s);
            alert.showAndWait();
        });
    }

    public void makeLoginAction(IoSession session) {
        javafx.application.Platform.runLater(() -> {
            try {
                changeViewToChat(session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void closeConnection() {
        javafx.application.Platform.runLater(() -> {
            cancelButton.fire();
        });
    }

    private void changeViewToChat(IoSession session) throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        cancelButton.getScene().getWindow().hide();
        System.out.println("changed to chat");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
        fxmlLoader.setController(ChatWindowController.getInstance());
        Parent root = null;
        root = fxmlLoader.load();
        stage.setTitle("ChatClient");
        stage.setScene(new Scene(root, 640, 480));
        stage.setOnCloseRequest(event -> {
            ConnectionManager.closeConnection(session);
            System.exit(0);
        });
        stage.show();
        System.out.println("end of changed to chat");
    }

    private boolean checkField(String text) {
        if (text.isEmpty() || text.contains(" ")) {
            return false;
        }
        return true;
    }
}
