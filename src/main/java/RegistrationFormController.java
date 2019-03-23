
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.mina.core.future.ReadFuture;

public class RegistrationFormController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Text errorText;

    private static RegistrationFormController instance;

    private RegistrationFormController() {

    }

    public static RegistrationFormController getInstance() {
        if (instance == null) {
            synchronized (RegistrationFormController.class) {
                if (instance == null) {
                    instance = new RegistrationFormController();
                }
            }
        }
        return instance;
    }

    @FXML
    void initialize() {
        errorText.setText("");
        cancelButton.setOnAction(event -> {
            ChatWindowController.getInstance().setRegistrationFormClosed();
            cancelButton.getScene().getWindow().hide();
        });
        addButton.setOnAction(event -> {
            String login = loginText.getText().trim();
            String password = passwordText.getText().trim();
            if (checkField(login) && checkField(password)) {
                ConnectionManager.getInstance().getSession().write(JsonMessageHandler.createRegistrationString(login, password));
            } else {
                if (login.split(" ").length > 1 || password.split(" ").length > 1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Error with input");
                    alert.setContentText("Your login or password contains blank spaces");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Error with input");
                    alert.setContentText("your login or password isn't specified");
                    alert.showAndWait();
                }
            }
        });
    }

    public void completeRegistration() {
        javafx.application.Platform.runLater(() -> {
            ChatWindowController.getInstance().setRegistrationFormClosed();
            errorText.getScene().getWindow().hide();
        });
    }

    public void setErrorText(String text) {
        errorText.setText("Error:" + text);
    }

    private boolean checkField(String text) {
        if (text.isEmpty() || text.contains(" ")) {
            return false;
        }
        return true;
    }
}
