package sharedmodel;

public class AuthData {
    public AuthData(String token, String username)
    {
        authToken = token;
        this.username = username;
    }

    public String authToken;
    public String username;
}
