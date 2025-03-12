package service;

import dataaccess.mem.MemGameDAO;
import dataaccess.mem.MemUserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.struct.AuthDAO;
import dataaccess.struct.GameDAO;
import dataaccess.struct.UserDAO;

public abstract class BaseService {
    public static UserDAO userAccess = new MemUserDAO();
    public static AuthDAO authAccess = new SQLAuthDAO();
    public static GameDAO gameAccess = new MemGameDAO();

    public BaseService()
    {

    }
}
