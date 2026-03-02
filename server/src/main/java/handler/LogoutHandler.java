package handler;

import io.javalin.http.Context;
import service.UserService;

/**
 * 3/1/26: added for p3 apis (logout)
 */
public class LogoutHandler {

    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");
        userService.logout(authToken);
        ctx.status(200).json(new Object());
    }
}
