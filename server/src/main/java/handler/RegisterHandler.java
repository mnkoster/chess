package handler;

import io.javalin.http.Context;
import requests.RegisterRequest;
import results.RegisterResult;
import service.UserService;

/**
 * 3/1/26: added for p3 apis (register)
 */
public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context ctx) {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        RegisterResult result = userService.register(
                request.username(),
                request.password(),
                request.email()
        );
        ctx.status(200).json(result);
    }
}
