package dataaccess.sql;

import dataaccess.struct.AuthDAO;
import dataaccess.struct.BaseSQLDAO;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class SQLAuthDAO extends BaseSQLDAO implements AuthDAO
{

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuth(UserData data)
    {
        String token = generateToken();

        executeSQL("INSERT INTO auth (username, token) VALUES ('"+data.username+"', '"+token+"');");

        return new AuthData(token, data.username);
    }

    public boolean deleteAuthData(String token)
    {
        try {
            executeSQL("DELETE FROM auth WHERE token='"+token+"';");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean tokenExists(String token)
    {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM auth WHERE token='" + token + "';")) {
            if (entries.next())
            {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public String getUsernameByToken(String token)
    {
        try (ResultSet entries = executeSQLQuery("SELECT (username) FROM auth WHERE token='" + token + "';"))
        {
            entries.next();
            return entries.getString(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<ArrayList<String>> getAllAsList()
    {
        ArrayList<ArrayList<String>> tokenLists = new ArrayList<>();

        try (ResultSet entries = executeSQLQuery("SELECT * FROM auth ORDER BY username LIMIT 0, 1000;"))
        {
            String currentUsername = "";
            ArrayList<String> currentUserTokens = new ArrayList<>();

            while (entries.next())
            {
                if (!entries.getString(1).equals(currentUsername))
                {
                    tokenLists.add(new ArrayList<>(currentUserTokens));
                    currentUserTokens.clear();
                    currentUsername = entries.getString(1);
                }

                currentUserTokens.add(entries.getString(2));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tokenLists;
    }

    public void reset()
    {
        executeSQL("TRUNCATE TABLE auth;");
    }

    public void setDB(Map<String, ArrayList<String>> value)
    {
        throw new RuntimeException("Not supported in SQL database mode.");
    }
}
