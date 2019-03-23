public final class ChatMessage {
    private String sender;
    private String time;
    private String message;

    public ChatMessage(String sender, String time, String message) {
        this.sender = sender;
        this.time = time;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
