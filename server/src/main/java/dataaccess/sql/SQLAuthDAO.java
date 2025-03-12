package dataaccess.sql;

import dataaccess.struct.AuthDAO;
import dataaccess.struct.BaseSQLDAO;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
            if (entries.next())
            {
                return entries.getString(1);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<ArrayList<String>> getAllAsList()
    {
        Map<String, ArrayList<String>> tokenLists = new HashMap<>();

        try (ResultSet entries = executeSQLQuery("SELECT * FROM auth ORDER BY username LIMIT 0, 1000;"))
        {
            ArrayList<String> currentUserTokens = new ArrayList<>();

            while (entries.next())
            {
                String username = entries.getString(1);
                String token = entries.getString(2);

                if (!tokenLists.containsKey(username))
                {
                    tokenLists.put(username, new ArrayList<String>());
                }

                tokenLists.get(username).add(token);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tokenLists.values();
    }

    public void reset()
    {
        executeSQL("TRUNCATE TABLE auth;");
    }

    public void setDB(Map<String, ArrayList<String>> value)
    {
        for (var userTokens : value.entrySet())
        {
            for (String token : userTokens.getValue())
            {
                executeSQL("INSERT INTO auth (username, token) VALUES ('"+userTokens.getKey()+"', '"+token+"');");
            }
        }
    }
}
