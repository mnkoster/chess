package handler;

import io.javalin.http.Context;
import requests.LoginRequest;
import results.LoginResult;
import service.UserService;

/**
 * 3/1/26: added handler for p3 apis (login)
 */
public class LoginHandler {

    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context ctx) {
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
    }
}
