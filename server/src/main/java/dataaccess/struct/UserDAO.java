package dataaccess.struct;

import exceptions.DataAccessException;
import model.UserData;

import java.util.Collection;

public interface UserDAO {
    public UserData getUser(String username) throws DataAccessException;

    public void setUser(UserData data) throws DataAccessException;

    public boolean userExists(UserData data);

    public Collection<UserData> getAllAsList();

    public boolean matchUsernamePassword(UserData data) throws DataAccessException;

    public void reset();
}
