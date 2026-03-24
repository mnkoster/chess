package client;

import ui.EscapeSequences;
import ui.State;
import java.util.Scanner;

/**
 * 3/24/26: added for p5 client - gameplay view
 */
public class GameplayRepl {

    private final Scanner scanner = new Scanner(System.in);
    private final boolean isWhitePerspective;

    public GameplayRepl(ClientSession session) {
        this.isWhitePerspective = session.playerWhite;
    }

    public State run() {
        System.out.println("=== Entered Game ===");
        printHelp();
        drawBoard();

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
                        drawBoard();
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
                    } else {
                        System.out.println("Leaving game...");
                        return State.LOGIN;
                    }
                }
                default -> System.out.println("Unknown command. Type 'help'");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
        Commands:
            - help                                      : Show available commands
            - redraw                                    : Redraw the board
            - exit                                      : Leave game (return to login)
        """);
    }

    private void drawBoard() {
        System.out.print(EscapeSequences.ERASE_SCREEN);

        int startRow = isWhitePerspective ? 8 : 1;
        int endRow   = isWhitePerspective ? 1 : 8;
        int rowStep  = isWhitePerspective ? -1 : 1;

        int startCol = isWhitePerspective ? 1 : 8;
        int endCol   = isWhitePerspective ? 8 : 1;
        int colStep  = isWhitePerspective ? 1 : -1;

        for (int row = startRow;
             isWhitePerspective ? row >= endRow : row <= endRow;
             row += rowStep) {
            // Row label
            System.out.print(" " + row + " ");

            for (int col = startCol;
                 isWhitePerspective ? col <= endCol : col >= endCol;
                 col += colStep) {

                boolean isLightSquare = (row + col) % 2 == 0;
                String bgColor = isLightSquare
                        ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                String piece = getPieceAt(row, col);
                System.out.print(bgColor + piece + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }
        // Column labels
        System.out.print("    ");
        for (int col = startCol;
             isWhitePerspective ? col <= endCol : col >= endCol;
             col += colStep) {
            char file = (char) ('a' + col - 1);
            System.out.print(" " + file + " ");
        }
        System.out.println();
    }

    private String getPieceAt(int row, int col) {
        // Pawns
        if (row == 2) return EscapeSequences.WHITE_PAWN;
        if (row == 7) return EscapeSequences.BLACK_PAWN;
        // Rooks
        if (row == 1 && (col == 1 || col == 8)) return EscapeSequences.WHITE_ROOK;
        if (row == 8 && (col == 1 || col == 8)) return EscapeSequences.BLACK_ROOK;
        // Knights
        if (row == 1 && (col == 2 || col == 7)) return EscapeSequences.WHITE_KNIGHT;
        if (row == 8 && (col == 2 || col == 7)) return EscapeSequences.BLACK_KNIGHT;
        // Bishops
        if (row == 1 && (col == 3 || col == 6)) return EscapeSequences.WHITE_BISHOP;
        if (row == 8 && (col == 3 || col == 6)) return EscapeSequences.BLACK_BISHOP;
        // Queens
        if (row == 1 && col == 4) return EscapeSequences.WHITE_QUEEN;
        if (row == 8 && col == 4) return EscapeSequences.BLACK_QUEEN;
        // Kings
        if (row == 1 && col == 5) return EscapeSequences.WHITE_KING;
        if (row == 8 && col == 5) return EscapeSequences.BLACK_KING;

        return EscapeSequences.EMPTY;
    }
}