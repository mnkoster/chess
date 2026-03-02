package server;

import io.javalin.*;
import dataaccess.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.json.JavalinGson;
import service.*;
import handler.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 2/28/26: added some logic
 * 3/1/26: added register, login, clear // service, handler, exceptions
 */
public class Server {

    private final Javalin javalin;

    public Server() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson(gson, false));
        });

        // Register your endpoints and exception handlers here.
        // DAOs
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        // Service
        AuthService authService = new AuthService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        // Handler
        RegisterHandler registerHandler = new RegisterHandler(authService);
        LoginHandler loginHandler = new LoginHandler(authService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        // Route
        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.delete("/db", clearHandler::handle);
        // Exception
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }); // 400: bad request
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        }); // 401: unauthorized (wrong username/password)
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        }); // 403: username already taken
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }); // 500: other errors
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
