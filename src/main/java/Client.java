
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Client extends Application {
    private static final int CONNECT_TIMEOUT = 5000;
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 9123;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        ConnectionManager.getInstance().StartConnection();
        LoginScreenController loginScreenController =LoginScreenController.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginScreen.fxml"));
        fxmlLoader.setController(loginScreenController);
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("ChatClient");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.setOnCloseRequest(event -> {
            loginScreenController.closeConnection();
            System.exit(0);
        });
        primaryStage.show();
    }
}
