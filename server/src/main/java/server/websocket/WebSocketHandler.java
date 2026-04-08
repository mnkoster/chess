package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import handler.ErrorResponse;
import jakarta.websocket.Session;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

/**
 * 4/7/26: added for p6 websocket - handler
 */
public class WebSocketHandler {

    private Gson gson;
    private final ConnectionManager connectionManager;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(ConnectionManager cm, GameDAO gameDAO, AuthDAO authDAO) {
        this.connectionManager = cm;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void onConnect(Session session) {

    }

    public void onClose(Session session) {
        // remove from ConnectionManager
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
        try {
            GameData game = gameDAO.getGame(gameID);

            ServerMessage loadMessage = new LoadGameMessage(game);
            send(session, loadMessage);

            ServerMessage notification = new NotificationMessage("Player joined game");
            connectionManager.broadcastToOthers(gameID, session, notification);
        } catch (Exception e) {
            throw new Exception("Error: websocket couldn't connect.");
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command) {
        // user actions: validate move, update game, save to DB, broadcase (load game, notification)
    }

    private void handleLeave(Session session, UserGameCommand command) {
        // user actions: remove from ConnectionManager, update DB if player, notify others
    }

    private void handleResign (Session session, UserGameCommand cmd) {
        // user actions: mark game over, save to DB, notify all
    }

    private void send(Session session, ServerMessage message) {

    }

    private void broadcastGameState(int gameID) {

    }

    private void sendError(Session session, String errorMessage) {
        try {
            ErrorResponse error = new ErrorResponse(errorMessage);
            String json = gson.toJson(error);
            session.getBasicRemote().sendText(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
