package dataaccess;

import model.AuthData;
import java.sql.ResultSet;

/**
 * 3/11/26: added for p4 database
 */
public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    // Configure the database
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        var createAuthTable = """
                CREATE TABLE IF NOT EXISTS authTokens(
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL
                )
                """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createAuthTable)) {
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to configure database", ex);
        }
    }

    // Interface function - added 3/11/26
    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var addAuth = """
                INSERT INTO authTokens (authToken, username)
                VALUES (?, ?)
                """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(addAuth)) {
            preparedStatement.setString(1, authData.authToken());
            preparedStatement.setString(2, authData.username());
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to create auth data", ex);
        }
    }

    // Interface function - added 3/11/26
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var getAuth = """
                SELECT authToken, username
                FROM authTokens
                WHERE authToken = ?
                """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(getAuth)) {
            preparedStatement.setString(1, authToken);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String authToken1 = rs.getString("authToken");
                    String username = rs.getString("username");
                    return new AuthData(authToken1, username);
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("unable to get auth", ex);
        }
        return null;
    }

    // Interface function - added 3/11/26
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var clearAuth = "DELETE FROM authTokens WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(clearAuth)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to clear given authToken", ex);
        }
    }

    // Interface function - added 3/11/26
    @Override
    public void clear() throws DataAccessException {
        var clearAuths = "DELETE FROM authTokens";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(clearAuths)) {
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to clear authTokens", ex);
        }
    }
}
