package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.JoinGameRequest;
import service.GameService;

/**
 * 3/2/26: added for p3 apis - join games
 * 3/10/26: updated for p4 database with DataAccessException
 */
public class JoinGameHandler {

    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
            gameService.joinGame(authToken, request.gameID(), request.playerColor());
            ctx.status(200).json(new Object());
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }
    }
}
