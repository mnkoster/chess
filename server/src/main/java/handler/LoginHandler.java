package handler;

import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requests.LoginRequest;
import results.LoginResult;
import service.UserService;

/**
 * 3/1/26: added p3 apis - login
 * 3/10/26: updated for p4 database with DataAccessException
 */
public class LoginHandler {

    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context ctx) {
        try {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

            if (request.username() == null || request.password() == null) {
                ctx.status(400).json(new ErrorResponse("Error: bad request"));
                return;
            }

            LoginResult result = userService.login(
                    request.username(),
                    request.password()
            );

            ctx.status(200).json(result);
        } catch (UnauthorizedException e) {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }
    }
}
