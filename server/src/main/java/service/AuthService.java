package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import handler.AlreadyTakenException;
import handler.BadRequestException;
import model.UserData;
import model.AuthData;
import results.LoginResult;
import dataaccess.UnauthorizedException;
import results.RegisterResult;

import java.util.UUID;

/**
 * 3/1/26: Added AuthService and login() for phase 3 apis
 */
public class AuthService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public AuthService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(String username, String password) {
        UserData user = userDAO.getUser(username);

        if (user == null || !user.password().equals(password)) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);
        return new LoginResult(username, token);
    }

    public RegisterResult register(String username, String password, String email) {
        if (username == null || password == null || email == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(username) != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        UserData user = new UserData(username, password, email);
        userDAO.addUser(user);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);
        return new RegisterResult(username, token);
    }
}
