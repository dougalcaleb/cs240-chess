package dataaccess.struct;

import dataaccess.DataAccessException;
import model.UserData;

import java.util.Collection;

public interface UserDAO {
    public UserData getUser(String username) throws DataAccessException;

    public void setUser(UserData data) throws DataAccessException;

    public void deleteUser(UserData data) throws DataAccessException;

    public boolean userExists(UserData data);

    public Collection<UserData> getAllAsList();
}
