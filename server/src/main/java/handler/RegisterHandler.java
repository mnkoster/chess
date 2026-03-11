package handler;

import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requests.RegisterRequest;
import results.RegisterResult;
import service.UserService;

/**
 * 3/1/26: added for p3 apis (register)
 * 3/11/26: finalized for p4 implementation
 */
public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context ctx) {
        try {
            RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
            RegisterResult result = userService.register(
                    request.username(),
                    request.password(),
                    request.email()
            );
            ctx.status(200).json(result);
        } catch (UnauthorizedException e) {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }
    }
}
