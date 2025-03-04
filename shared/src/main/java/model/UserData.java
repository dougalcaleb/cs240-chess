package model;

import java.util.Objects;

public class UserData {
    public String username;
    public String password;
    public String email;

    public UserData(String username, String password, String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserData userData = (UserData) o;
        return Objects.equals(username, userData.username) && Objects.equals(password, userData.password) && Objects.equals(email, userData.email);
    }
}
