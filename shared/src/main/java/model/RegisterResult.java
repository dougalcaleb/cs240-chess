package model;

public class RegisterResult {
    public RegisterResult(boolean create, boolean login, boolean exists, AuthData token)
    {
        createSucceeded = create;
        loginSucceeded = login;
        userExists = exists;
        this.token = token;
    }

    public RegisterResult() {}

    public boolean createSucceeded;
    public boolean loginSucceeded;
    public boolean userExists;
    public AuthData token;
}
