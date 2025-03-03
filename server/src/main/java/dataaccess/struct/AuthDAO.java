package dataaccess.struct;

import model.AuthData;
import model.UserData;

import java.util.Collection;

public interface AuthDAO {

    public AuthData createAuth(UserData data);

    public AuthData getAuthData(String username);

    public boolean deleteAuthData(AuthData data);

    public boolean deleteAuthData(String token);

    public Collection<String> getAllAsList();

    public boolean tokenExists(String token);

    public String getUsernameByToken(String token);
}
