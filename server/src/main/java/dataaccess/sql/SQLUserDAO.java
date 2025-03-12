package dataaccess.sql;

import dataaccess.struct.BaseSQLDAO;
import dataaccess.struct.UserDAO;
import exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class SQLUserDAO extends BaseSQLDAO implements UserDAO {
    @Override
    public void setDB(Map<String, UserData> value) {
        for (var entry : value.entrySet())
        {
            executeSQL("INSERT INTO users (username, password, email) VALUES ('"+entry.getValue().username+"', '"+entry.getValue().password+"', '"+entry.getValue().email+"');");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM users WHERE username='" + username + "';")) {
            entries.next();
            return new UserData(
                entries.getString(1),
                entries.getString(2),
                entries.getString(3)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setUser(UserData data) throws DataAccessException {
        executeSQL("INSERT INTO users (username, password, email) VALUES ('"+data.username+"', '"+data.password+"', '"+data.email+"');");
    }

    @Override
    public boolean userExists(UserData data) {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM users WHERE username='" + data.username + "';")) {

            if (entries.next())
            {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public Collection<UserData> getAllAsList() {
        ArrayList<UserData> data = new ArrayList<>();

        try (ResultSet entries = executeSQLQuery("SELECT * FROM users LIMIT 0, 1000;")) {
            while (entries.next())
            {
                data.add(new UserData(
                    entries.getString(1),
                    entries.getString(2),
                    entries.getString(3)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    @Override
    public boolean matchUsernamePassword(UserData data) throws DataAccessException {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM users WHERE username='" + data.username + "';")) {
            if (entries.next())
            {
                return BCrypt.checkpw(data.password, entries.getString(2));
            } else {
                throw new DataAccessException("Could not get user - user does not exist");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reset() {
        executeSQL("TRUNCATE TABLE users;");
    }
}
