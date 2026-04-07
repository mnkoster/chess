package server.websocket;

import jakarta.websocket.Session;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Set;

/**
 * 4/7/26: added for p6 websocket - websocket connections
 */
public class ConnectionManager {

    Map<Integer, Set<Session>> gameConnections;

    public void addConnection(int gameID, Session session) {

    }

    public void removeConnection(int gameID, Session session) {

    }

    public void broadcastToGame(int gameID, ServerMessage message) {

    }

    public void broadcastToOthers(int gameID, Session sender, ServerMessage message) {

    }

    public Set<Session> getConnections(int gameID) {
        return gameConnections.get(gameID);
    }
}
