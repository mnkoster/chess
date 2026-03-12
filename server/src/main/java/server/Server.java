package server;

import io.javalin.Javalin;
import io.javalin.json.JavalinGson;

import dataaccess.*;
import service.*;
import handler.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {

    private final Javalin javalin;

    public Server() {

        Gson gson = new GsonBuilder().serializeNulls().create();

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson(gson, false));
        });

        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database failed to initialize", e);
        }

        // DAOs
        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;

        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database failed to initialize", e);
        }

        // Services
        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        GameService gameService = new GameService(gameDAO, authDAO);

        // Handlers
        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // Routes
        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.delete("/session", logoutHandler::handle);
        javalin.get("/game", listGamesHandler::handle);
        javalin.post("/game", createGameHandler::handle);
        javalin.put("/game", joinGameHandler::handle);
        javalin.delete("/db", clearHandler::handle);

        // Exceptions
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        });

        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        });

        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        });

        javalin.exception(Exception.class, (_, ctx) -> {
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