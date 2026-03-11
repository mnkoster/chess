package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;

/**
 * 3/1/26: added getUser() for p3 apis
 */
public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void addUser(UserData user) throws DataAccessException;
    void clear() throws DataAccessException;
}
