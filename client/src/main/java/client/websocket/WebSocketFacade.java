package client.websocket;

import chess.ChessPosition;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import java.net.URI;

/**
 * 4/7/26: added for p6 websocket - handle
 */
public class WebSocketFacade {

    private Session session;
    private final Gson gson = new Gson();
    private final NotificationHandler notifyHandler;
    private final URI serverUri;

    public WebSocketFacade(String url, NotificationHandler handler) throws Exception {
        this.serverUri = new URI(url);
        this.notifyHandler = handler;
    }

    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(String.class, this::onMessage);
        System.out.println("WebSocket connected " + config);
    }

    public void open() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                WebSocketFacade.this.onOpen(session, config);
            }
        }, serverUri);
    }

    public void connect(String authToken, int gameID) throws Exception {
        // call addConnection in server.ConnectionManager
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );
        send(command);
    }

    public void disconnect() {
        // call removeConnection in server.ConnectionManager
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            System.out.println("Couldn't close connection");
        }
    }

    private void send(UserGameCommand command) throws Exception {
        // serialize send json
        try {
            String json = gson.toJson(command);
            session.getAsyncRemote().sendText(json);
        } catch (Exception e) {
            throw new Exception("Failed to send message in socket");
        }
    }

    private void sendMove(MakeMoveCommand command) throws Exception {
        // serialize send json
        try {
            String json = gson.toJson(command);
            session.getAsyncRemote().sendText(json);

        } catch (Exception e) {
            throw new Exception("Failed to send message in socket");
        }
    }

    public void makeMove(String authToken, int gameID, MakeMoveCommand.MoveDTO move) throws Exception {
        // user makes move
        MakeMoveCommand command = new MakeMoveCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID,
                move
        );
        // attach move later
        sendMove(command);
    }

    public void resign(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        );

        send(command);
    }

    public void leave(String authToken, int gameID) throws Exception {
        // user leaves, does not cause resign
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );

        send(command);
    }

    private void onMessage(String message) {
        // deserialize -> ServerMessage
        // pass to NotificationHandler.handle()
        ServerMessage base = gson.fromJson(message, ServerMessage.class);

        switch (base.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage msg = gson.fromJson(message, LoadGameMessage.class);
                notifyHandler.handleLoadGame(msg);
            }
            case NOTIFICATION -> {
                NotificationMessage msg = gson.fromJson(message, NotificationMessage.class);
                notifyHandler.handleNotification(msg);
            }
            case ERROR -> {
                ServerErrorMessage msg = gson.fromJson(message, ServerErrorMessage.class);
                notifyHandler.handleError(msg);
            }
        }
    }
}
