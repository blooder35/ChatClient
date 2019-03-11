
import com.sun.glass.ui.Window;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class ClientHandler extends IoHandlerAdapter {
  private static final String USER_LIST_METHOD = "userStatus";
  private static final String MESSAGE_METHOD = "message";
  private static final String REGISTRATION_METHOD = "registration";
  private static final String LOGIN_METHOD="login";
  private static ChatWindowController chatWindowController;
  private static RegistrationFormController registrationFormController;
  private static LoginScreenController loginScreenController;

  public ClientHandler(ChatWindowController chatWindowController, RegistrationFormController registrationFormController, LoginScreenController loginScreenController) {
    super();
    this.chatWindowController = chatWindowController;
    this.registrationFormController = registrationFormController;
    this.loginScreenController=loginScreenController;
  }

  @Override
  public void sessionOpened(IoSession session) throws Exception {
    System.out.println("sessionOpened");
  }

  @Override
  public void messageReceived(IoSession session, Object message) throws Exception {
    //System.out.println(message);
    if (message.toString().substring(0, 10).equals(USER_LIST_METHOD)) {
      chatWindowController.addUserToUserList(message.toString().substring(11));
    } else if (message.toString().substring(0, 7).equals(MESSAGE_METHOD)) {
      //if (message != null)
        chatWindowController.addReceivedMessageToChat(message);
    } else if (message.toString().substring(0, 12).equals(REGISTRATION_METHOD)) {
      //chatWindowController.registrationFormAnswer(message);
        registrationFormController.checkAnswer(message.toString().substring(13));
    } else if (message.toString().substring(0, 5).equals(LOGIN_METHOD)) {
      loginScreenController.makeLoginAction(loginScreenController,message,session);
    }
  }

  @Override
  public void sessionClosed(IoSession session) throws Exception {
    super.sessionClosed(session);
    javafx.application.Platform.runLater(()->{
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setHeaderText("Error!!");
      alert.setContentText("Connection to server was lost, your application will be closed");
      alert.showAndWait();
      javafx.application.Platform.exit();
      System.exit(0);
    });

  }

  @Override
  public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    super.exceptionCaught(session, cause);
  }
}
