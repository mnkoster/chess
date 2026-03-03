package handler;

import io.javalin.http.Context;
import requests.JoinGameRequest;
import service.GameService;

/**
 * 3/2/26: added for p3 apis (join games)
 */
public class JoinGameHandler {

    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");
        JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
        gameService.joinGame(authToken, request.gameID(), request.playerColor());
        ctx.status(200).json(new Object());
    }
}
