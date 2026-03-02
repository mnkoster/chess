package handler;

import io.javalin.http.Context;
import requests.CreateGameRequest;
import service.GameService;

import java.util.Map;

/**
 * 3/2/26: added for p3 apis (games)
 */
public class CreateGameHandler {

    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context ctx) {

        String authToken = ctx.header("Authorization");
        CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
        int gameID = gameService.createGame(authToken, request.gameName());
        ctx.status(200).json(Map.of("gameID", gameID));
    }
}
