package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.UserData;
import model.AuthData;
import results.LoginResult;
import dataaccess.UnauthorizedException;

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
}
