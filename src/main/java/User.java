public final class User {
    private String username;
    private int status;

    public User(String username, int status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public int getStatus() {
        return status;
    }
}
