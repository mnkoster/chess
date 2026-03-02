package server;

import io.javalin.*;
import dataaccess.*;
import service.*;
import handler.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        // DAOs
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        // Service
        AuthService authService = new AuthService(userDAO, authDAO);
        // Handler
        LoginHandler loginHandler = new LoginHandler(authService);
        // Route
        javalin.post("/session", loginHandler::handle);

        // Exception
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        });
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
