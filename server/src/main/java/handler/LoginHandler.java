package handler;

import io.javalin.http.Context;
import requests.LoginRequest;
import results.LoginResult;
import service.AuthService;

/**
 * 3/1/26: added handler for p3 apis (login)
 */
public class LoginHandler {

    private final AuthService authService;

    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }

    public void handle(Context ctx) {
        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

        if (request.username() == null || request.password() == null) {
            ctx.status(400).json(new ErrorResponse("Error: bad request"));
            return;
        }
        LoginResult result = authService.login(
                request.username(),
                request.password()
        );

        ctx.status(200).json(result);
    }
}
