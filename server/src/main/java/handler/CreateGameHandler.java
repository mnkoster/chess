package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.CreateGameRequest;
import service.GameService;

import java.util.Map;

/**
 * 3/2/26: added for p3 apis - games
 * 3/10/26: updated for p4 database with DataAccessException
 */
public class CreateGameHandler {

    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
            int gameID = gameService.createGame(authToken, request.gameName());
            ctx.status(200).json(Map.of("gameID", gameID));
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }
    }
}
