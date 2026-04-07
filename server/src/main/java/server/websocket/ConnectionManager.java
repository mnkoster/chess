package server.websocket;

import jakarta.websocket.Session;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 4/7/26: added for p6 websocket - websocket connections
 */
public class ConnectionManager {

    Map<Integer, Set<Session>> gameConnections;

    public void addConnection(int gameID, Session session) {
        gameConnections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void removeConnection(int gameID, Session session) {

    }

    public void broadcastToGame(int gameID, ServerMessage message) {
        for (Session s : gameConnections.getOrDefault(gameID, Set.of())) {
            send(s, message);
        }
    }

    public void broadcastToOthers(int gameID, Session sender, ServerMessage message) {
        for (Session s : gameConnections.getOrDefault(gameID, Set.of())) {
            if (!s.equals(sender)) {
                send(s, message);
            }
        }
    }

    public Set<Session> getConnections(int gameID) {
        return gameConnections.get(gameID);
    }
}
