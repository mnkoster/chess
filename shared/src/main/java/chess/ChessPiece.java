package chess;

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

        // PAWN
        if (piece.getPieceType() == PieceType.PAWN) {
            // fill in
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
        return List.of(); // added 1/20/26, phase 0 video UPDATE
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
