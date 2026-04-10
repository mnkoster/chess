package client;

import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;
import ui.State;
import client.websocket.WebSocketFacade;
import client.websocket.NotificationHandler;
import websocket.commands.MakeMoveCommand;

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
    private ChessPosition mostRecentStart;
    private ChessPosition mostRecentEnd;

    public ChessPosition getMostRecentEnd() {
        return mostRecentEnd;
    }

    public ChessPosition getMostRecentStart() {
        return mostRecentStart;
    }

    public GameplayRepl(ClientSession session) throws Exception {
        this.clientSession = session;
        this.isWhitePerspective = !clientSession.getPlayerType().equals(ClientSession.PlayerTypes.PLAYER_BLACK);
        NotificationHandler notifyHandler = new NotificationHandler();
        this.websocket = new WebSocketFacade("ws://localhost:4444/ws", notifyHandler);
        notifyHandler.setGameplayRepl(this);
        this.mostRecentStart = null;
        this.mostRecentEnd = null;

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

    /**
     * Run loop
     */
    public State run() {
        try {
            websocket.open();
            websocket.connect(clientSession.authToken, clientSession.gameplayID);
        } catch (Exception e) {
            System.out.println("Failed to connect to game");
            return State.LOGIN;
        }
        System.out.println("=== Entered Game ===");

        while (true) {
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "redraw" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else {
                        drawBoard(null, null);
                        System.out.println("[GAME] >>> ");
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
                        ChessPiece.PieceType promoPiece = null;
                        if (tokens.length == 4) {
                            promoPiece = determinePromoPiece(tokens[3]);
                        }
                        MakeMoveCommand.MoveDTO currMove = new MakeMoveCommand.MoveDTO();
                        currMove.start = new MakeMoveCommand.Position();
                        currMove.end = new MakeMoveCommand.Position();
                        currMove.start.row = start.getRow();
                        currMove.start.col = start.getColumn();
                        currMove.end.row = end.getRow();
                        currMove.end.col = end.getColumn();
                        currMove.promoType = promoPiece;
                        websocket.makeMove(clientSession.authToken, clientSession.gameplayID, currMove);
                        mostRecentStart = start;
                        mostRecentEnd = end;
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
                        try {
                            ChessPosition pos = parsePosition(tokens[1]);
                            drawBoardHighlight(pos);
                        } catch (Exception e) {
                            System.out.println("Invalid position");
                        }
                    }
                }
                default -> System.out.println("Unknown command. Type 'help'");
            }
        }
    }

    public void setGame(GameData game) {
        this.game = game;
    }

    private ChessPiece.PieceType determinePromoPiece(String input) {
        switch (input) {
            case "KNIGHT" -> {
                return ChessPiece.PieceType.KNIGHT;
            }
            case "BISHOP" -> {
                return ChessPiece.PieceType.BISHOP;
            }
            case "ROOK" -> {
                return ChessPiece.PieceType.ROOK;
            }
            case "QUEEN" -> {
                return ChessPiece.PieceType.QUEEN;
            }
            default -> {
                return null;
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
        System.out.print("[GAME] >>> ");
    }

    private ChessPosition parsePosition(String input) {
        if (input == null || input.length() < 2) {
            throw new IllegalArgumentException("Invalid position: " + input);
        }
        input = input.trim().toLowerCase();
        char file = input.charAt(0);
        if (file < 'a' || file > 'h') {
            throw new IllegalArgumentException("Invalid column: " + file);
        }
        int col = file - 'a' + 1;
        int row;
        try {
            row = Integer.parseInt(input.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row: " + input.substring(1));
        }
        if (row < 1 || row > 8) {
            throw new IllegalArgumentException("Row out of bounds: " + row);
        }
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
                String bgColor;
                if (new ChessPosition(row, col).equals(startPos)) {
                    bgColor = isLightSquare
                            ? EscapeSequences.SET_BG_COLOR_LIGHT_GREEN
                            : EscapeSequences.SET_BG_COLOR_LIGHTER_GREEN;
                } else if (new ChessPosition(row, col).equals(endPos)) {
                    bgColor = isLightSquare
                            ? EscapeSequences.SET_BG_COLOR_DARKER_GREEN
                            : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                } else {
                    bgColor = isLightSquare
                            ? EscapeSequences.SET_BG_COLOR_GREY
                            : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                }
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
        if (game == null || game.game() == null) {
            return EscapeSequences.EMPTY;
        }

        var board = game.game().getBoard();
        var piece = board.getPiece(new ChessPosition(row, col));

        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        return switch (piece.getPieceType()) {
            case PAWN -> piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_PAWN
                    : EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_PAWN;

            case ROOK -> piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_ROOK
                    : EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_ROOK;

            case KNIGHT -> piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_KNIGHT
                    : EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_KNIGHT;

            case BISHOP -> piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_BISHOP
                    : EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_BISHOP;

            case QUEEN -> piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_QUEEN
                    : EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_QUEEN;

            case KING -> piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_KING
                    : EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.BLACK_KING;
        };
    }

    public void drawBoardHighlight(ChessPosition position) {
        if (game == null || game.game() == null) {
            drawBoard(null, null);
            return;
        }

        // Get legal moves for that position
        var board = game.game().getBoard();
        var piece = board.getPiece(position);

        if (piece == null) {
            System.out.println("No piece at that position.");
            return;
        }

        var moves = game.game().validMoves(position);

        ArrayList<ChessPosition> highlightSquares = new ArrayList<>();
        for (var move : moves) {
            highlightSquares.add(move.getEndPosition());
        }

        drawBoardWithHighlights(position, highlightSquares);
    }

    private void drawBoardWithHighlights(ChessPosition selected,
                                         ArrayList<ChessPosition> highlights) {

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
             isWhitePerspective ? row >= endRow : row <= endRow;
             row += rowStep) {

            System.out.print(" " + row + " ");

            for (int col = startCol;
                 isWhitePerspective ? col <= endCol : col >= endCol;
                 col += colStep) {

                ChessPosition current = new ChessPosition(row, col);
                boolean isLightSquare = (row + col) % 2 == 0;

                String bgColor;
                if (current.equals(selected)) {
                    bgColor = isLightSquare
                            ? EscapeSequences.SET_BG_COLOR_LIGHT_GREEN
                            : EscapeSequences.SET_BG_COLOR_LIGHTER_GREEN;

                } else if (highlights.contains(current)) {
                    bgColor = isLightSquare
                            ? EscapeSequences.SET_BG_COLOR_DARK_BLUE
                            : EscapeSequences.SET_BG_COLOR_BLUE;

                    // Normal board
                } else {
                    bgColor = isLightSquare
                            ? EscapeSequences.SET_BG_COLOR_GREY
                            : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                }

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
}