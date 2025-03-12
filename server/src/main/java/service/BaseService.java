package service;

import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import dataaccess.struct.AuthDAO;
import dataaccess.struct.GameDAO;
import dataaccess.struct.UserDAO;

public abstract class BaseService {
    public static UserDAO userAccess = new SQLUserDAO();
    public static AuthDAO authAccess = new SQLAuthDAO();
    public static GameDAO gameAccess = new SQLGameDAO();

    public BaseService()
    {

    }
}
