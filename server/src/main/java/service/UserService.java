package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import handler.AlreadyTakenException;
import handler.BadRequestException;
import model.UserData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;
import results.LoginResult;
import dataaccess.UnauthorizedException;
import results.RegisterResult;

import java.util.UUID;

/**
 * 3/1/26: added for p3 apis (AuthService, login, logout)
 * 3/10/26: updated for p4 database (DataAccessExceptions)
 */
public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(String username, String password) throws DataAccessException {
        UserData user;
        user = userDAO.getUser(username);

        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);
        return new LoginResult(username, token);
    }

    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData possibleUserName;
        possibleUserName = userDAO.getUser(username);
        if (possibleUserName != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData user = new UserData(username, hashPassword, email);
        userDAO.addUser(user);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);
        return new RegisterResult(username, token);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}
