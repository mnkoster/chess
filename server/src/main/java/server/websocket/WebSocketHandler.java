package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import model.GameData;
import websocket.commands.MakeMoveCommand;
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
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    handleMakeMove(session, moveCommand);
                }
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
            send(session, new ServerErrorMessage("invalid gameID"));
            return;
        }
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) {
            send(session, new ServerErrorMessage("invalid authentication"));
            return;
        }
        String username = authDAO.getUsername(command.getAuthToken());
        send(session, new LoadGameMessage(game));
        connectionManager.broadcastToOthers(gameID, session, new NotificationMessage(username + " entered the game"));
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) throws Exception {
        // user actions: validate move, update game, save to DB, broadcast (load game, notification)
        // ENTRY
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            send(session, new ServerErrorMessage("invalid gameID"));
            return;
        }
        String auth = command.getAuthToken();
        String username = authDAO.getUsername(auth);
        if (!(game.whiteUsername().equals(username) || game.blackUsername().equals(username))) {
            send(session, new ServerErrorMessage("observer cannot make moves"));
            return;
        }
        // MAKE SURE GAME IS NOT OVER
        if (game.isGameOver()) {
            send(session, new ServerErrorMessage("game is over, cannot make moves"));
            return;
        }
        // VALIDATE USER
        ChessGame.TeamColor currColor = game.game().getTeamTurn();
        if (game.whiteUsername().equals(username) && currColor != ChessGame.TeamColor.WHITE) {
            send(session, new ServerErrorMessage("not white's turn"));
            return;
        }
        if (game.blackUsername().equals(username) && currColor != ChessGame.TeamColor.BLACK) {
            send(session, new ServerErrorMessage("not black's turn"));
            return;
        }
        // MAKE MOVE
        ChessPosition startPos = command.getMove().getStartPosition();
        ChessPosition endPos = command.getMove().getEndPosition();
        ChessPiece.PieceType promo = command.getMove().getPromotionPiece();
        try {
            game.game().makeMove(new ChessMove(startPos, endPos, promo));
        } catch (InvalidMoveException e) {
            send(session, new ServerErrorMessage("invalid move"));
            return;
        }
        ChessGame chess = game.game();
        String winner = null;

        boolean whiteCheckmate = chess.isInCheckmate(ChessGame.TeamColor.WHITE);
        boolean blackCheckmate = chess.isInCheckmate(ChessGame.TeamColor.BLACK);
        boolean whiteStalemate = chess.isInStalemate(ChessGame.TeamColor.WHITE);
        boolean blackStalemate = chess.isInStalemate(ChessGame.TeamColor.BLACK);
        boolean gameOver = whiteCheckmate || blackCheckmate || whiteStalemate || blackStalemate;
        if (whiteCheckmate) {
            winner = "BLACK";
        } else if (blackCheckmate) {
            winner = "WHITE";
        }
        GameData updatedGame = new GameData(
                game.gameID(),
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game(),
                gameOver
        );
        gameDAO.updateGame(updatedGame);
        broadcastGameState(gameID);
        // move notifications
        connectionManager.broadcastToOthers(
                gameID,
                session,
                new NotificationMessage(username + " made move: " + startPos.toString() + endPos.toString())
        );
        // game over notifications
        if (gameOver) {
            if (winner == null) {
                connectionManager.broadcastToGame(
                        gameID,
                        new NotificationMessage("game over: stalemate")
                );
            } else {
                connectionManager.broadcastToGame(
                        gameID,
                        new NotificationMessage("game over: " + winner + " won")
                );
            }
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        // user actions: remove from ConnectionManager, update DB if player, notify others
        try {
            int gameID = command.getGameID();
            GameData game = gameDAO.getGame(gameID);
            if (game == null) {
                sendError(session, "invalid gameID");
                return;
            }
            String authToken = command.getAuthToken();
            String username = authDAO.getUsername(authToken);
            String white = game.whiteUsername();
            String black = game.blackUsername();
            if (username.equals(white)) {
                white = null;
            } else if (username.equals(black)) {
                black = null;
            }
            GameData updatedGame = new GameData(
                    game.gameID(),
                    white,
                    black,
                    game.gameName(),
                    game.game(),
                    true
            );

            gameDAO.updateGame(updatedGame);
            connectionManager.removeConnection(gameID, session);
            connectionManager.broadcastToOthers(
                    gameID,
                    session,
                    new NotificationMessage(username + " left the game")
            );

        } catch (Exception e) {
            sendError(session, "Error leaving game");
        }
    }

    private void handleResign (Session session, UserGameCommand command) throws Exception {
        // user actions: mark game over, save to DB, notify all
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            send(session, new ServerErrorMessage("invalid gameID"));
            return;
        }
        String auth = command.getAuthToken();
        String username = authDAO.getUsername(auth);
        if (!(game.whiteUsername().equals(username) || game.blackUsername().equals(username))) {
            send(session, new ServerErrorMessage("observer cannot make moves"));
            return;
        }

        gameDAO.updateGame(game);
        connectionManager.broadcastToGame(
                gameID,
                new NotificationMessage(username + " resigned. Game over.")
        );
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
