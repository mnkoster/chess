package dataaccess;

import model.UserData;

/**
 * 3/1/26: added for p3 apis - getUser()
 */
public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void addUser(UserData user) throws DataAccessException;
    void clear() throws DataAccessException;
}
