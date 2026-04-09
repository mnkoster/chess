package client;

import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;
import ui.State;
import client.websocket.WebSocketFacade;
import client.websocket.NotificationHandler;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 3/24/26: added for p5 client - gameplay view
 * 4/6/26: added for p6 websocket - added help commands
 */
public class GameplayRepl {

    private final Scanner scanner = new Scanner(System.in);
    private final boolean isWhitePerspective;
    ArrayList<String> helpList;
    // Websocket
    private final WebSocketFacade websocket;
    private final ClientSession clientSession;
    private GameData game;

    public GameplayRepl(ClientSession session) throws Exception {
        this.clientSession = session;
        this.isWhitePerspective = !clientSession.getPlayerType().equals(ClientSession.PlayerTypes.PLAYER_BLACK);
        NotificationHandler notifyHandler = new NotificationHandler();
        this.websocket = new WebSocketFacade("ws://localhost:4444/ws", notifyHandler);
        notifyHandler.setGameplayRepl(this);

        // Help List extended
        helpList = new ArrayList<>();
        helpList.add("   |   Commands: ");
        helpList.add("   |   help                                    : Show available commands");
        if (!clientSession.getPlayerType().equals(ClientSession.PlayerTypes.OBSERVER)) {
            helpList.add("   |   move <START> <END> {PROMO PIECE}        : Make move");
            helpList.add("   |   resign                                  : Resign from game");
        }
        helpList.add("   |   redraw                                  : Redraw the board");
        helpList.add("   |   highlight <position>                    : Highlight legal moves of piece at position");
        helpList.add("   |   exit                                    : Leave game (return to login)");
    }

    public State run() {
        try {
            websocket.open();
            websocket.connect(clientSession.authToken, clientSession.gameplayID);
        } catch (Exception e) {
            System.out.println("Failed to connect to game");
            return State.LOGIN;
        }
        System.out.println("=== Entered Game ===");
        printHelp();
        drawBoard(null, null);

        while (true) {
            System.out.print("\n[GAME] >>> ");
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "redraw" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else {
                        drawBoard(null, null);
                    }
                }
                case "help" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else {
                        printHelp();
                    }
                }
                case "exit" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        break;
                    }
                    try {
                        websocket.leave(clientSession.authToken, clientSession.gameplayID);
                        websocket.disconnect();
                    } catch (Exception e) {
                        System.out.println("Error leaving game");
                    }
                    System.out.println("Leaving game...");
                    return State.LOGIN;
                }
                // added cases
                case "move" -> {
                    if (tokens.length != 3 && tokens.length != 4) {
                        System.out.println("Usage: move <start> <end> [promo letter]");
                        break;
                    }
                    try {
                        ChessPosition start = parsePosition(tokens[1]);
                        ChessPosition end = parsePosition(tokens[2]);
                        // add promotion piece logic
                        // ChessPiece promotionPiece = new ChessPiece(session.playerWhite, tokens[3]);
                        ChessMove currMove = new ChessMove(start, end, null);
                        websocket.makeMove(clientSession.authToken, clientSession.gameplayID, currMove);
                    } catch (Exception e) {
                        String raw = e.getMessage();
                        String cleaned = raw.replaceAll(".*\"message\":\"", "")
                                .replaceAll("\".*", "").replaceFirst("^Error:\\s*", "");
                        System.out.println("Invalid move: " + cleaned);
                    }
                }
                case "resign" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        break;
                    }
                    try {
                        websocket.resign(clientSession.authToken, clientSession.gameplayID);
                    } catch (Exception e) {
                        System.out.println("Failed to resign");
                    }
                }
                case "highlight" -> {
                    if (tokens.length != 2) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        System.out.println("Usage: highlight <position>");
                    } else {
                        System.out.println("Implement highlight please");
                    }
                }
                default -> System.out.println("Unknown command. Type 'help'");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
        Commands:
        - help                                      : Show available commands""");
        if (clientSession.getPlayerType().equals(ClientSession.PlayerTypes.OBSERVER)) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREY);
        }
        System.out.println("""
        - move                                      : Make move
        - resign                                    : Resign from game""");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.println("""
        - redraw                                    : Redraw the board
        - highlight                                 : Highlight legal moves
        - exit                                      : Leave game (return to login)
        """);
    }

    private ChessPosition parsePosition(String input) {
        int col = input.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(input.charAt(1));
        return new ChessPosition(row, col);
    }

    private void drawColumn() {
        int startCol = isWhitePerspective ? 1 : 8;
        int endCol   = isWhitePerspective ? 8 : 1;
        int colStep  = isWhitePerspective ? 1 : -1;

        System.out.print(" ");
        for (int col = startCol;
             isWhitePerspective ? col <= endCol : col >= endCol;
             col += colStep) {
            char file = (char) ('a' + col - 1);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " \u2003  " + file);
        }
        System.out.println();
    }

    public void drawBoard(ChessPosition startPos, ChessPosition endPos) {
        System.out.print(EscapeSequences.ERASE_SCREEN);

        int startRow = isWhitePerspective ? 8 : 1;
        int endRow   = isWhitePerspective ? 1 : 8;
        int rowStep  = isWhitePerspective ? -1 : 1;
        int startCol = isWhitePerspective ? 1 : 8;
        int endCol   = isWhitePerspective ? 8 : 1;
        int colStep  = isWhitePerspective ? 1 : -1;

        drawColumn();
        int i = 0;
        for (int row = startRow;
             isWhitePerspective ? row >= endRow : row <= endRow; row += rowStep) {
            System.out.print(" " + row + " ");

            for (int col = startCol;
                 isWhitePerspective ? col <= endCol : col >= endCol;
                 col += colStep) {

                boolean isLightSquare = (row + col) % 2 == 0;
                String bgColor = isLightSquare
                        ? EscapeSequences.SET_BG_COLOR_GREY
                        : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                String piece = getPieceAt(row, col);
                System.out.print(bgColor + piece + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + " ");
            if (i < helpList.size()) {
                System.out.print(helpList.get(i));
                i++;
            }
            System.out.println();
        }
        drawColumn();
    }

    private String getPieceAt(int row, int col) {
        // Pawns
        if (row == 2) { return EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_PAWN; }
        if (row == 7) { return EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_PAWN; }
        // Rooks
        if (row == 1 && (col == 1 || col == 8)) { return EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_ROOK; }
        if (row == 8 && (col == 1 || col == 8)) { return EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_ROOK; }
        // Knights
        if (row == 1 && (col == 2 || col == 7)) { return EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_KNIGHT; }
        if (row == 8 && (col == 2 || col == 7)) { return EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_KNIGHT; }
        // Bishops
        if (row == 1 && (col == 3 || col == 6)) { return EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_BISHOP; }
        if (row == 8 && (col == 3 || col == 6)) { return EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_BISHOP; }
        // Queens
        if (row == 1 && col == 4) { return EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_QUEEN; }
        if (row == 8 && col == 4) { return EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_QUEEN; }
        // Kings
        if (row == 1 && col == 5) { return EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_KING; }
        if (row == 8 && col == 5) { return EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_KING; }

        return EscapeSequences.EMPTY;
    }
}