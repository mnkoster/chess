package handler;

import io.javalin.http.Context;
import requests.RegisterRequest;
import results.RegisterResult;
import handler.ErrorResponse;
import service.AuthService;

/**
 * 3/1/26: added for p3 apis (register)
 */
public class RegisterHandler {

    private final AuthService authService;

    public RegisterHandler(AuthService authService) {
        this.authService = authService;
    }

    public void handle(Context ctx) {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        RegisterResult result = authService.register(
                request.username(),
                request.password(),
                request.email()
        );
        ctx.status(200).json(result);
    }
}
