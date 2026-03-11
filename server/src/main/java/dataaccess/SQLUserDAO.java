package dataaccess;

import model.UserData;
import java.sql.ResultSet;

/**
 * 3/10/26: added for p4 database set up
 */
public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    // Configure the database
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        var createUserTable = """
                CREATE TABLE IF NOT EXISTS users(
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
                )
                """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createUserTable)) {
             preparedStatement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to configure database", ex);
        }
    }

    // Interface function - added 3/10/26
    @Override
    public UserData getUser(String username) throws DataAccessException {
        var getUser = """
                SELECT username, password, email
                FROM user
                WHERE username = ?
                """;

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(getUser)) {
            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String username1 = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    return new UserData(username1, password, email);
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("unable to get user", ex);
        }
        return null;
    }

    // Interface function - added 3/10/26
    @Override
    public void addUser(UserData user) throws DataAccessException {
        var addUser = """
                INSERT INTO users (username, password, email)
                VALUES (?, ?, ?)
                """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(addUser)) {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to create user", ex);
        }
    }

    // Interface function - added 3/10/26
    @Override
    public void clear() throws DataAccessException {
        var clearUser = "DELETE FROM users";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(clearUser)) {
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to clear users", ex);
        }
    }
}
