package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

/**
 * 3/1/26: added getUser() and addUser() for p3 apis
 */
public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void addUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
