package dataaccess.struct;

import exceptions.DataAccessException;
import sharedmodel.UserData;

import java.util.Collection;
import java.util.Map;

public interface UserDAO {

    public void setDB(Map<String, UserData> value);

    public UserData getUser(String username) throws DataAccessException;

    public void setUser(UserData data) throws DataAccessException;

    public boolean userExists(UserData data);

    public Collection<UserData> getAllAsList();

    public boolean matchUsernamePassword(UserData data) throws DataAccessException;

    public void reset();
}
