
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
import org.apache.mina.core.future.ReadFuture;
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
  private ChatWindowController chatWindowController;
  private RegistrationFormController registrationFormController;

  @FXML
  void initialize() {
    //connect to server
    chatWindowController = new ChatWindowController();
    registrationFormController = new RegistrationFormController();
    ConnectionManager cm = new ConnectionManager(chatWindowController, registrationFormController, this);
    chatWindowController.setConnectionManager(cm);
    chatWindowController.setRegistrationFormController(registrationFormController);
    registrationFormController.setConnectionManager(cm);

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
      //cm.DisconnectFromServer();
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
          cm.getSession().write("login " + loginString + " " + passwordString);
        }
      } else {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText("Your name or password is not specified or contains blank spaces!");
        alert.setHeaderText("Error in Fields");
        alert.showAndWait();
      }
    });

  }

  boolean checkField(String text) {
    if (text.isEmpty() || text.contains(" ")) {
      return false;
    }
    return true;
  }

  public void errorWhileLoggingIn(String s) {
    System.out.println("errorwhileloggin in");
    javafx.application.Platform.runLater(()-> {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setHeaderText("Error while logging in");
      alert.setContentText("message from server: " + s);
      alert.showAndWait();
    });
  }

  public synchronized void changeViewToChat(IoSession session) throws IOException {
    Stage stage = (Stage) cancelButton.getScene().getWindow();
    cancelButton.getScene().getWindow().hide();
    System.out.println("changed to chat");
    //moved to top
//    ChatWindowController chatWindowController=new ChatWindowController(cm);
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
    fxmlLoader.setController(chatWindowController);
    Parent root = fxmlLoader.load();
    //Parent root = FXMLLoader.load(getClass().getResource("ChatWindow.fxml"));
    stage.setTitle("ChatClient");
    stage.setScene(new Scene(root, 640, 480));
    stage.setOnCloseRequest(event -> {
      ConnectionManager.closeConnection(session);
      System.exit(0);
    });
    stage.show();
    System.out.println("end of changed to chat");
  }

  public synchronized void makeLoginAction(LoginScreenController loginScreenController, Object message, IoSession session) {
    String[] strArr = message.toString().split(" ");
    String loginString = loginText.getText().trim();
    session.setAttribute("login", loginString);
    System.out.println(loginString + "whaaat");
    if (strArr.length > 0 && strArr[0].equals("login")) {
      if (strArr[1].equals("granted")) {
        javafx.application.Platform.runLater(() -> {
          try {
            loginScreenController.changeViewToChat(session);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
      } else {
        loginScreenController.errorWhileLoggingIn(strArr[1]);
      }
    }
  }

  public void closeConnection() {
    javafx.application.Platform.runLater(() -> {
      cancelButton.fire();
    });
  }
}
