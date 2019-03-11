
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
  private ConnectionManager cm;
  private RegistrationFormController registrationFormController;
  private static boolean registrationFormOpened;

  public ChatWindowController(ConnectionManager cm) {
    this.cm = cm;
  }

  private Map<String, Boolean> userList = new TreeMap<>();
  private Font FONT = new Font("Arial", 25);

  public ChatWindowController() {
    registrationFormOpened = false;
  }

  public static void setRegistrationFormClosed() {
    registrationFormOpened = false;
  }


  @FXML
  void initialize() {
    //map with users as a key and boolean online status


    System.out.println("Chat window controller started");
    cm.getSession().write("getMessageHistory");

    exitButton.setOnAction(event -> {
      cm.DisconnectFromServer();
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
    if (cm.getSession().getAttribute("login").equals("admin"))
      addUserButton.setDisable(false);
    addUserButton.setOnAction(event -> {
      try {
        showRegistrationForm();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

  }

  void checkAndSendMessage(String message) {
    if (!message.isEmpty()) {
      Date date=new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      SimpleDateFormat dateFormatToSend = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String timeToSend = dateFormatToSend.format(date);
      System.out.println(timeToSend);
      String time = dateFormat.format(date);
      cm.getSession().write("message " + time + " " + message);
      userInputField.setText("");
      textAreaField.appendText(cm.getSession().getAttribute("login") + " [" + timeToSend + "]:\n" + message + "\n\n");
    }
  }

  void showRegistrationForm() throws IOException {
    if (!registrationFormOpened) {
      registrationFormOpened = true;
      Stage stage = new Stage();
      //RegistrationFormController registrationFormController = new RegistrationFormController(cm);
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegistrationForm.fxml"));
      fxmlLoader.setController(registrationFormController);
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

  public synchronized void addReceivedMessageToChat(Object message) {
    Platform.runLater(() -> {
      textAreaField.appendText(message.toString().substring(8).replaceAll("//&&//", "\n"));
    });
  }

  public void setConnectionManager(ConnectionManager cm) {
    this.cm = cm;
  }

  public void setRegistrationFormController(RegistrationFormController registrationFormController) {
    this.registrationFormController = registrationFormController;
  }

  public synchronized void addUserToUserList(Object message) {
    String[] strArr = message.toString().split(" ");
    String username = "";
    //по сути не нужно, но пусть будет
    if (strArr.length == 2) {
      username = strArr[0];
      int status = Integer.parseInt(strArr[1]);
      boolean online = (status == 1);
      userList.put(username, online);
    }
    //по сути тоже не нужно
    if (!username.equals("") && userList.containsKey(username)) {
      displayUsers();
    } else {
      displayUser(username);
    }
  }

  public synchronized void displayUsers() {
    javafx.application.Platform.runLater(() -> {
      listOfUsersField.getChildren().clear();
    });
    for (String entry : userList.keySet()) {
      displayUser(entry);
    }
  }

  public synchronized void displayUser(String username) {
    Text t = new Text(username + "\n");
    //t.setFont(FONT);
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

