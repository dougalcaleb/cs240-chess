package model;

public class RegisterResult {
    public RegisterResult(boolean create, boolean login, boolean exists, String token, String username)
    {
        createSucceeded = create;
        loginSucceeded = login;
        userExists = exists;
        authToken = token;
    }

    public RegisterResult() {}

    public boolean createSucceeded;
    public boolean loginSucceeded;
    public boolean userExists;
    public String authToken;
    public String username;

    public String getUsername()
    {
        return username;
    }

    public String getAuthToken()
    {
        return authToken;
    }
}
