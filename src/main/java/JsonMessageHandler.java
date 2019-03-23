import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonMessageHandler {

    public static String createLoginString(String username, String password) {
        LoginCredentials lc= new LoginCredentials(username, password);
        GsonBuilder builder=new GsonBuilder();
        Gson gson=builder.create();
        SocketMessage sm = new SocketMessage(MethodContainer.LOGIN_ACTION, gson.toJson(lc));
        return gson.toJson(sm);
    }

    public static String createGetMessageHistoryString() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SocketMessage sm = new SocketMessage(MethodContainer.GET_MESSAGE_HISTORY_METHOD, "");
        return gson.toJson(sm);
    }
    public static String createMessageString(String time,String message){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SocketMessage sm = new SocketMessage(MethodContainer.MESSAGE_METHOD, gson.toJson(new ChatMessageToSend(time, message)));
        return gson.toJson(sm);
    }

    public static String createRegistrationString(String username, String password) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        LoginCredentials lc = new LoginCredentials(username, password);
        SocketMessage sm = new SocketMessage(MethodContainer.REGISTRATION_METHOD, gson.toJson(lc));
        return gson.toJson(sm);
    }


}
