package client.websocket;

import chess.ChessMove;
import websocket.commands.UserGameCommand;

/**
 * 4/7/26: added for p6 websocket - handle
 */
public class WebSocketFacade {

    public void connect(String authToken, int gameID) {
        // call addConnection in server.ConnectionManager
    }

    public void disconnect() {
        // call removeConnection in server.ConnectionManager
    }

    private void send(UserGameCommand cmd) {
        // serialize send json
    }

    public void makeMove(String authToken, int gameID, ChessMove move) {
        // user makes move
    }

    public void resign(String authToken, int gameID) {
        // user resigns, does not go back to login
    }

    public void leave(String authToken, int gameID) {
        // user leaves, does not cause resign
    }

    private void onMessage(String jsonMessage) {
        // deserialize -> ServerMessage
        // pass to NotificationHandler.handle()
    }
}
