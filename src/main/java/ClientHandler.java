
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.Alert;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public final class ClientHandler extends IoHandlerAdapter {

    public ClientHandler() {
        super();
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("sessionOpened");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        System.out.println(message.toString());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SocketMessage socketMessage = gson.fromJson(message.toString(), SocketMessage.class);
        if (socketMessage.getMethod() == null || socketMessage.getBody() == null) {
            System.out.println("some packet is wrong and not supported");
        } else {
            switch (socketMessage.getMethod()) {
                case MethodContainer.LOGIN_METHOD:
                    LoginStatusBody loginStatusBody = gson.fromJson(socketMessage.getBody(), LoginStatusBody.class);
                    if (loginStatusBody.getStatus() != null && loginStatusBody.getUsername() != null) {
                        if (loginStatusBody.getStatus().equals(MethodContainer.LOGIN_GRANTED)) {
                            session.setAttribute(MethodContainer.SESSION_ATTRIBUTE_LOGIN, loginStatusBody.getUsername());
                            LoginScreenController.getInstance().makeLoginAction(session);
                        } else {
                            LoginScreenController.getInstance().errorWhileLoggingIn(loginStatusBody.getStatus());
                        }
                    }
                    break;
                case MethodContainer.MESSAGE_METHOD:
                    ChatMessage chatMessage = gson.fromJson(socketMessage.getBody(), ChatMessage.class);
                    ChatWindowController.getInstance().addReceivedMessageToChat(chatMessage);
                    break;
                case MethodContainer.USER_STATUS_METHOD:
                    User user = gson.fromJson(socketMessage.getBody(), User.class);
                    ChatWindowController.getInstance().addUserToUserList(user);
                    break;
                case MethodContainer.REGISTRATION_METHOD:
                    if (socketMessage.getBody().equals(MethodContainer.REGISTRATION_SUCCESS)) {
                        RegistrationFormController.getInstance().completeRegistration();
                    } else {
                        RegistrationFormController.getInstance().setErrorText(socketMessage.getBody());
                    }
                    break;
            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        javafx.application.Platform.runLater(() -> {
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
