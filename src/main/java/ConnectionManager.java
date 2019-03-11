
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ConnectionManager implements ConnectionConfig {

  private IoSession session;
  private ChatWindowController chatWindowController;
  private RegistrationFormController registrationFormController;
  private LoginScreenController loginScreenController;

  public static void closeConnection(IoSession session) {
    if (session != null) {
      session.closeNow();
    }
  }

  public IoSession getSession() {
    return session;
  }

  public ConnectionManager(ChatWindowController chatWindowController, RegistrationFormController registrationFormController, LoginScreenController loginScreenController) {
    this.registrationFormController = registrationFormController;
    this.chatWindowController = chatWindowController;
    this.loginScreenController=loginScreenController;
    StartConnection();
  }

  public void StartConnection(){
    try {
      this.session = connectAndGetSession(CONNECT_TIMEOUT, HOSTNAME, PORT);
    } catch (RuntimeIoException e){
      this.session=null;
    }
  }
  public void DisconnectFromServer(){
    if (this.session!=null) {
      this.session.closeNow();
    }
    System.out.println("Connection closed");
  }

  private IoSession connectAndGetSession(int CONNECT_TIMEOUT, String HOSTNAME, int PORT) throws RuntimeIoException{
    NioSocketConnector connector=new NioSocketConnector();
    connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
    TextLineCodecFactory textLineCodecFactory=new TextLineCodecFactory(Charset.forName("UTF-8"));
    textLineCodecFactory.setDecoderMaxLineLength(MAXSTRING);
    textLineCodecFactory.setEncoderMaxLineLength(MAXSTRING);
    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(textLineCodecFactory));
    connector.setHandler(new ClientHandler(chatWindowController,registrationFormController,loginScreenController));
    for (; ; ) {
      try {
        ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
        future.awaitUninterruptibly();
        session=future.getSession();
        break;
      } catch (RuntimeIoException e) {
        System.err.println("Failed to connect");
        e.printStackTrace();
        throw new RuntimeIoException();
      }
    }
    return session;
  }

}
