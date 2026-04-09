package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 4/7/26: added for p6 gameplay
 */
public class ConnectionManager {

    private final Map<Integer, Set<Session>> gameConnections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void addConnection(int gameID, Session session) {
        gameConnections
                .computeIfAbsent(gameID, k -> new CopyOnWriteArraySet<>())
                .add(session);
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
        Set<Session> sessions = gameConnections.get(gameID);
        if (sessions == null) { return; }
        for (Session session : sessions) {
            send(session, message);
        }
    }

    public void broadcastToOthers(int gameID, Session sender, ServerMessage message) {
        Set<Session> sessions = gameConnections.get(gameID);

        if (sessions == null) { return; }

        for (Session session : sessions) {
            if (!session.equals(sender)) {
                send(session, message);
            }
        }
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