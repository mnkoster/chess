package server;

import io.javalin.*;
import dataaccess.*;
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

        // DAOs
        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            userDAO = new MemoryUserDAO();
            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
        }

        // Service
        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        GameService gameService = new GameService(gameDAO, authDAO);

        // Handler
        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // Route
        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.delete("/session", logoutHandler::handle);
        javalin.get("/game", listGamesHandler::handle);
        javalin.post("/game", createGameHandler::handle);
        javalin.put("/game", joinGameHandler::handle);
        javalin.delete("/db", clearHandler::handle);
//       } catch (DataAccessException e) {
//            throw new RuntimeException("Database failed", e);
//        }
        // Exceptions
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }); // 400: bad request
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(500).json(new ErrorResponse("Error: Internal Error"));
        }); // 401: unauthorized (wrong username/password)
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        }); // 403: username already taken
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new ErrorResponse("Error: Internal Error"));
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
