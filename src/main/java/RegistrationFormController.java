
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

  ConnectionManager cm;
  public RegistrationFormController(ConnectionManager cm){
    this.cm=cm;
  }

  public RegistrationFormController() {

  }

  @FXML
  void initialize() {
    errorText.setText("");
    cancelButton.setOnAction(event -> {
      ChatWindowController.setRegistrationFormClosed();
      cancelButton.getScene().getWindow().hide();
    });
    addButton.setOnAction(event -> {
      String login=loginText.getText().trim();
      String password=passwordText.getText().trim();
      if (checkField(login) && checkField(password)) {
        //send request and wait for responce;
        cm.getSession().write("registration "+login+" "+password);
//        cm.getSession().getConfig().setUseReadOperation(true);
//        ReadFuture future = cm.getSession().read();
//        future.awaitUninterruptibly();
//        Object message=null;
//        try{
//          message=future.getMessage();
//          System.out.println("first message:" + message);
//          message=future.getMessage();
//          System.out.println("second message:" + message);
//        } catch (Exception e){
//          System.err.println("exception while reading");
//        }
//        if(message!=null){
//          System.out.println(message);
//          //check responce;
//          String code=message.toString().substring(0,12);
//          String answer=message.toString().substring(13);
//          System.out.println(code+"\n"+answer);
//          System.out.println("REGISTRATION FORM ANSWER:"+message);
//          if (code.equals("registration")) {
//            if (answer.equals("completed")) {
//              addButton.getScene().getWindow().hide();
//              Alert alert = new Alert(Alert.AlertType.WARNING);
//              alert.setHeaderText("Success");
//              alert.setContentText("Registration completed successfully");
//              alert.showAndWait();
//            } else {
//              Alert alert = new Alert(Alert.AlertType.WARNING);
//              alert.setHeaderText("Error");
//              alert.setContentText(answer);
//              alert.showAndWait();
//            }
//          }
//        }
      } else {
        if (login.split(" ").length > 1 || password.split(" ").length > 1) {
          Alert alert=new Alert(Alert.AlertType.WARNING);
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
  boolean checkField(String text){
    if (text.isEmpty() || text.contains(" ")) {
      return false;
    }
    return true;
  }

  public void checkAnswer(String message) {
    if (message.equals("completed")) {
      errorText.setText(message);
      javafx.application.Platform.runLater(() ->{
        ChatWindowController.setRegistrationFormClosed();
        errorText.getScene().getWindow().hide();
      });
    } else {
      errorText.setText("Error:" +message);
    }
  }
  public void setConnectionManager(ConnectionManager cm) {
    this.cm = cm;
  }
}
