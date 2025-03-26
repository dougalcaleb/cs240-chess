package dataaccess.struct;

import sharedmodel.AuthData;
import sharedmodel.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public interface AuthDAO {

    public void setDB(Map<String, ArrayList<String>> value);

    public AuthData createAuth(UserData data);

    public boolean deleteAuthData(String token);

    public Collection<ArrayList<String>> getAllAsList();

    public boolean tokenExists(String token);

    public String getUsernameByToken(String token);

    public void reset();
}
