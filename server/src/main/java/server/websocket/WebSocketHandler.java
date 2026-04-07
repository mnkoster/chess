package server.websocket;

import jakarta.websocket.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

/**
 * 4/7/26: added for p6 websocket - handler
 */
public class WebSocketHandler {

    public void onConnect(Session session) {

    }

    public void onClose(Session session) {
        // remove from ConnectionManager
    }

    public void onMessage(Session session, String message) {
        // deserialize UserGameCommand
        // route to correct handler
    }

    private void handleConnect(Session session, UserGameCommand cmd) {
        // user actions: add to ConnectionManager, send LOAD_GAME to client, notify others
    }

    private void handleMakeMove(Session session, UserGameCommand command) {
        // user actions: validate move, update game, save to DB, broadcase (load game, notification)
    }

    private void handleLeave(Session session, UserGameCommand cmd) {
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

    }
}
