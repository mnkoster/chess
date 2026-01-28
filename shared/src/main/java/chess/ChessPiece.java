package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * equality and hashcode
     * updated 1/27/26 from p0 implementation
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    /**
     * equality and hashcode
     * updated 1/27/26 from p0 implementation
     */
    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     * added 1/27/26 from p0 implementation - updated for quality
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     * added 1/27/26 from p0 implementation - updated for quality
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     * added 1/27/26 from p0 implementation - updated for quality
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        // KING
        if (piece.type == PieceType.KING) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    moves.addAll(straightMoves(board, piece, myPosition, i, j, false));
                }
            }
        }
        // QUEEN
        if (piece.type == PieceType.QUEEN) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    moves.addAll(straightMoves(board, piece, myPosition, i, j, true));
                }
            }
        }
        // BISHOP
        if (piece.type == PieceType.BISHOP) {
            moves.addAll(straightMoves(board, piece, myPosition, 1, 1, true));
            moves.addAll(straightMoves(board, piece, myPosition, 1, -1, true));
            moves.addAll(straightMoves(board, piece, myPosition, -1, 1, true));
            moves.addAll(straightMoves(board, piece, myPosition, -1, -1, true));
        }
        // KNIGHT
        if (piece.type == PieceType.KNIGHT) {
            moves = knightMoves(board, piece, myPosition);
        }
        // ROOK
        if (piece.type == PieceType.ROOK) {
            moves.addAll(straightMoves(board, piece, myPosition, 1, 0, true));
            moves.addAll(straightMoves(board, piece, myPosition, -1, 0, true));
            moves.addAll(straightMoves(board, piece, myPosition, 0, 1, true));
            moves.addAll(straightMoves(board, piece, myPosition, 0, -1, true));
        }
        // PAWN
        if (piece.type == PieceType.PAWN) {
            // Black Pawns
            if (piece.pieceColor == ChessGame.TeamColor.BLACK) {
                if (myPosition.getRow() == 7) { // starting row
                    moves = pawnMove(board, 2, -1, myPosition); // board, maxSpaces, direction
                } else {
                    moves = pawnMove(board, 1, -1, myPosition);
                }
                moves.addAll(pawnCapture(board, piece, -1, myPosition));
            }
            // White Pawns
            if (piece.pieceColor == ChessGame.TeamColor.WHITE) {
                if (myPosition.getRow() == 2) { // starting row
                    moves = pawnMove(board, 2, 1, myPosition); // board, maxSpaces, direction
                } else {
                    moves = pawnMove(board, 1, 1, myPosition);
                }
                moves.addAll(pawnCapture(board, piece, 1, myPosition));
            }
        }
        return moves;
    }

    /**
     * helper function for spaces
     * added 1/27/26 from p0 implementation - updated for quality
     */
    private boolean inBound(int row, int col) {
        return (row >= 1 & row <= 8 & col >= 1 & col <= 8);
    }

    /**
     * helper function for spaces
     * added 1/27/26 from p0 implementation - updated for quality
     */
    private boolean openPosition(ChessBoard board, ChessPosition position) {
        return (board.getPiece(position) == null);
    }

    /**
     * helper function for spaces
     * added 1/27/26 from p0 implementation - updated for quality
     */
    private boolean capturable(ChessPiece piece, ChessPiece other) {
        return (piece.pieceColor != other.pieceColor);
    }

    /**
     * Pawn spaces, promotion or null
     * Added 1/28/26 from p0 implementation - updated for quality
     */
    private Collection<ChessMove> pawnPromo(int checkRow, ChessPosition myPosition, ChessPosition checkPos) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (checkRow == 1 || checkRow == 8) { // promotion
            moves.add(new ChessMove(myPosition, checkPos, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, checkPos, PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, checkPos, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, checkPos, PieceType.ROOK));
        } else {
            moves.add(new ChessMove(myPosition, checkPos, null));
        }
        return moves;
    }

    /**
     * Checks for pawn spaces
     * added 1/27/26 from p0 implementation - updated for quality
     */
    private Collection<ChessMove> pawnMove(ChessBoard board, int maxSpaces, int direction, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        for (int i = 1; i < maxSpaces + 1; i++) {
            int checkRow = currRow + (i * direction);

            if (inBound(checkRow, currCol)) {
                ChessPosition checkPos = new ChessPosition(checkRow, currCol);
                if (openPosition(board, checkPos)) {
                    moves.addAll(pawnPromo(checkRow, myPosition, checkPos));
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    /**
     * Pawn help to add promotions
     * added 1/27/26 for quality
     */
    private Collection<ChessMove> pawnCaptureHelp(ChessBoard board, ChessPiece piece, ChessPosition myPosition,
                                                int checkRow, int checkCol) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (inBound(checkRow, checkCol)) {
            ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
            if (!openPosition(board, checkPos)) {
                ChessPiece other = board.getPiece(checkPos);
                if (capturable(piece, other)) {
                    moves.addAll(pawnPromo(checkRow, myPosition, checkPos));
                }
            }
        }
        return moves;
    }

    /**
     * Checks for pawnCapture spaces
     * added 1/27/26 from p0 implementation - updated for quality
     */
    private Collection<ChessMove> pawnCapture(ChessBoard board, ChessPiece piece, int direction, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        int checkRow = currRow + direction;
        int leftCol = currCol - 1;
        int rightCol = currCol + 1;

        moves.addAll(pawnCaptureHelp(board, piece, myPosition, checkRow, leftCol));
        moves.addAll(pawnCaptureHelp(board, piece, myPosition, checkRow, rightCol));
        return moves;
    }

    /**
     * Checks for knight spaces
     * added 1/27/26 from p0 implementation - updated for quality
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        int[][] possible = {{currRow + 2, currCol + 1}, {currRow + 2, currCol - 1},
                {currRow + 1, currCol + 2}, {currRow + 1, currCol - 2},
                {currRow - 1, currCol + 2}, {currRow - 1, currCol - 2},
                {currRow - 2, currCol + 1}, {currRow - 2, currCol - 1}};

        for (int i = 0; i < 8; i++) {
            int checkRow = possible[i][0];
            int checkCol = possible[i][1];

            if (inBound(checkRow, checkCol)) {
                ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
                if (openPosition(board, checkPos)) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                } else {
                    ChessPiece other = board.getPiece(checkPos);
                    if (capturable(piece, other)) {
                        moves.add(new ChessMove(myPosition, checkPos, null));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Checks for straight/diagonal spaces
     * added 1/27/26 from p0 implementation - updated for quality
     * updated 1/28/26 for conciseness/quality
     */
    private Collection<ChessMove> straightMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition,
                                                int rowOffset, int colOffset, boolean slide) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        int i = 1;
        int checkRow = currRow + (i*rowOffset);
        int checkCol = currCol + (i*colOffset);

        if (slide) {
            while (inBound(checkRow, checkCol) && openPosition(board, new ChessPosition(checkRow, checkCol))) {
                ChessPosition otherPos = new ChessPosition(currRow + (i*rowOffset), currCol + (i*colOffset));
                moves.add(new ChessMove(myPosition, otherPos, null));
                i++;
                checkRow = currRow + (i*rowOffset);
                checkCol = currCol + (i*colOffset);
            }
        }
        if (inBound(checkRow, checkCol)) {
            ChessPosition otherPos = new ChessPosition(checkRow, checkCol);
            if (openPosition(board, otherPos) || capturable(piece, board.getPiece(otherPos))) {
                moves.add(new ChessMove(myPosition, otherPos, null));
            }
        }
        return moves;
    }
}