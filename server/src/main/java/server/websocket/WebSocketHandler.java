package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

/**
 * 4/7/26: added for p6 websocket - handler
 */
public class WebSocketHandler {

    private final Gson gson = new Gson();
    private final ConnectionManager connectionManager;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(ConnectionManager connManager, GameDAO gameDAO, AuthDAO authDAO) {
        this.connectionManager = connManager;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void onConnect(Session session) {
        System.out.println("Websocket connected: " + session);
    }

    public void onClose(Session session) {
        // remove from ConnectionManager
        connectionManager.removeSessionFromAllGames(session);
    }

    public void onMessage(Session session, String message) {
        // deserialize UserGameCommand, route to correct handler
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                case MAKE_MOVE -> handleMakeMove(session, command);
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command);
            }
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws Exception {
        // user actions: add to ConnectionManager, send LOAD_GAME to client, notify others
        int gameID = command.getGameID();
        connectionManager.addConnection(gameID, session);
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new Exception("Game not found");
        }
        send(session, new LoadGameMessage(game));
        String username = authDAO.getUsername(command.getAuthToken());
        connectionManager.broadcastToOthers(gameID, session, new NotificationMessage(username + " entered the game"));
    }

    private void handleMakeMove(Session session, UserGameCommand command) throws Exception {
        // user actions: validate move, update game, save to DB, broadcast (load game, notification)
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new Exception("Game not found");
        }
        // Extend UserGameCommand makemove

        // Validate move

        gameDAO.updateGame(game);
        broadcastGameState(gameID);
        connectionManager.broadcastToOthers(
                gameID,
                session,
                new NotificationMessage("UPDATE: A move was made")
        );
    }

    private void handleLeave(Session session, UserGameCommand command) {
        // user actions: remove from ConnectionManager, update DB if player, notify others
        int gameID = command.getGameID();
        connectionManager.removeConnection(gameID, session);
        connectionManager.broadcastToOthers(
                gameID,
                session,
                new NotificationMessage("UPDATE: A player left the game")
        );
    }

    private void handleResign (Session session, UserGameCommand command) throws Exception {
        // user actions: mark game over, save to DB, notify all
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new Exception("Game not found");
        }
        // set game as over
        gameDAO.updateGame(game);
        connectionManager.broadcastToOthers(gameID, session, new NotificationMessage("UPDATE: A player resigned. Game over."));
    }

    private void send(Session session, ServerMessage message) throws Exception {
        try {
            if (session.isOpen()) {
                String json = gson.toJson(message);
                session.getRemote().sendString(json);
            }
        } catch (Exception e) {
            throw new Exception("Error sending notification");
        }
    }

    private void broadcastGameState(int gameID) throws Exception {
        GameData game = gameDAO.getGame(gameID);

        connectionManager.broadcastToGame(
                gameID,
                new LoadGameMessage(game)
        );
    }

    private void sendError(Session session, String errorMessage) {
        try {
            ServerErrorMessage error = new ServerErrorMessage(errorMessage);
            String json = gson.toJson(error);
            session.getRemote().sendString(json);
        } catch (Exception e) {
            System.out.println("This is websocket handler, error");
        }
    }
}
