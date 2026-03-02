package dataaccess;

import model.UserData;

/**
 * 3/1/26: added getUser() for p3 apis
 */
public interface UserDAO {
    UserData getUser(String username);
    void addUser(UserData user);
    void clear();
}
