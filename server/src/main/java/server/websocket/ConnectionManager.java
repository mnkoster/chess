package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 4/7/26: added for p6 websocket - websocket connections
 */
public class ConnectionManager {

    private final Map<Integer, Set<Session>> gameConnections = new HashMap<>();
    private final Gson gson = new Gson();

    public void addConnection(int gameID, Session session) {
        gameConnections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void removeConnection(int gameID, Session session) {
        Set<Session> sessions = gameConnections.get(gameID);

        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
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

    private void send(Session session, ServerMessage message) {
        try {
            if (session.isOpen()) {
                String json = gson.toJson(message);
                session.getRemote().sendString(json);
            }
        } catch (Exception e) {
            try {
                session.close();
            } catch (Exception ex) {
                removeSessionFromAllGames(session);
            }
        }
    }

    public void removeSessionFromAllGames(Session session) {
        for (Integer gameID : gameConnections.keySet()) {
            removeConnection(gameID, session);
        }
    }
}
