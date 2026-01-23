package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor; // added 1/20/26, phase 0 video
    private final PieceType type; // added 1/20/26, phase 0 video

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor; // added 1/20/26, phase 0 video
        this.type = type; // added 1/20/26, phase 0 video
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
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor; // added 1/20/26, phase 0 video
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type; // added 1/20/26, phase 0 video
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        // PAWN
        if (piece.getPieceType() == PieceType.PAWN) {
            if (piece.pieceColor == ChessGame.TeamColor.BLACK) {
                if (start_row == 7) { moves = pawnMoves(board, 2, -1, myPosition); }
                else { moves = pawnMoves(board, 1, -1, myPosition); }
                moves.addAll(pawnCapture(board, piece, -1, myPosition));
            }
            if (piece.pieceColor == ChessGame.TeamColor.WHITE) {
                if (start_row == 2) { moves = pawnMoves(board, 2, 1, myPosition); }
                else { moves = pawnMoves(board, 1, 1, myPosition); }
                moves.addAll(pawnCapture(board, piece, 1, myPosition));
            }
        }
        // ROOK
        if (piece.getPieceType() == PieceType.ROOK) {
            // fill in
        }
        // KNIGHT
        if (piece.getPieceType() == PieceType.KNIGHT) {
            // fill in
        }
        // BISHOP
        if (piece.getPieceType() == PieceType.BISHOP) {
            // fill in
        }
        // QUEEN
        if (piece.getPieceType() == PieceType.QUEEN) {
            // fill in
        }
        // KING
        if (piece.getPieceType() == PieceType.KING) {
            // fill in
        }
        return moves; // added 1/20/26, phase 0 video UPDATE
    }

    // ********************** PIECE MOVE LOGIC *******************************************

    /**
     * Check if space is out of bounds before making move
     * added 1/23/26 for piece moves
     */
    private boolean inBound(int row, int col) {
        return (1 <= row & row <= 8 & 1 <= col & col <= 8);
    }

    /**
     * General check if position is open on the board
     * added 1/22/26 for piece moves
     */
    private boolean openPosition(ChessBoard board, ChessPosition checkPosition) {
        ChessPiece piece = board.getPiece(checkPosition);
        return piece == null;
    }

    /**
     * Check if unavailable space is enemy/capturable
     * added 1/22/26 for piece moves
     */
    private boolean capturable(ChessPiece piece, ChessPiece other) {
        return piece.pieceColor != other.pieceColor;
    }


    /**
     * Checks for pawn movement
     * added 1/22/26 for piece moves
     * updated 1/23/26 for pawn specific
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, int maxSpaces, int direction, ChessPosition myPosition) {
        Collection<ChessMove> spaces = new ArrayList<>();
        // row and column integers for reference
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        // check if space ahead up to maxSpaces (1/2 for rook, 1 for king, to edge for rest) is clear
        for (int i = 1; i < maxSpaces+1; i++) {
            int row_check = row + (i*direction);
            if (!inBound(row_check, col)) { // check space is valid on board
                break;
            }
            ChessPosition check = new ChessPosition(row_check, col);
            if (!openPosition(board, check)) { // check if space is open
                break;
            }
            spaces.add(new ChessMove(myPosition, new ChessPosition(row + (i*direction), col), null));
        }
        return spaces;
    }

    /**
     * Capture for pawn movement
     * added 1/23/26 for pawn specific
     */
    private Collection<ChessMove> pawnCapture(ChessBoard board, ChessPiece piece, int direction, ChessPosition myPosition) {
        int row_check = myPosition.getRow() + direction;
        int left_col = myPosition.getColumn() - 1;
        int right_col = myPosition.getColumn() + 1;
        Collection<ChessMove> captureSpace = new ArrayList<>();
        if (inBound(row_check, left_col)) {
            ChessPosition checkPosition = new ChessPosition(row_check, left_col);
            if (!openPosition(board, checkPosition)) { // check if space is open
                ChessPiece other = board.getPiece(checkPosition);
                if (capturable(piece, other)) { captureSpace.add(new ChessMove(myPosition, checkPosition, null)); }
            }
        }
        if (inBound(row_check, right_col)) {
            ChessPosition checkPosition = new ChessPosition(row_check, right_col);
            if (!openPosition(board, checkPosition)) { // check if space is open
                ChessPiece other = board.getPiece(checkPosition);
                if (capturable(piece, other)) { captureSpace.add(new ChessMove(myPosition, checkPosition, null)); }
            }
        }
        return captureSpace;
    }

    /**
     * equals override
     * checks that each piece on board is equivalent
     * added 1/21/26 to pass ChessBoardTests
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    /**
     * hashcode override
     * added 1/21/26 to pass ChessBoardTests
     */
    @Override
    public int hashCode() {
        int result = pieceColor.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
