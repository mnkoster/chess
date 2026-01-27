package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition; // added 1/20/26, phase 0 video
    private final ChessPosition endPosition; // added 1/20/26, phase 0 video
    private final ChessPiece.PieceType promotionPiece; // added 1/20/26, phase 0 video

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition; // added 1/20/26, phase 0 video
        this.endPosition = endPosition; // added 1/20/26, phase 0 video
        this.promotionPiece = promotionPiece; // added 1/20/26, phase 0 video
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition; // added 1/20/26, phase 0 video
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition; // added 1/20/26, phase 0 video
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece; // added 1/20/26, phase 0 video
    }


    /**
     * toString for readability in debugging
     * added 1/20/26, phase 0 video
     */
    @Override
    public String toString() {
        return String.format("%s%s", startPosition, endPosition);
    }

    /**
     * equality and hashcode
     * updated 1/27/26 from p0 implementation
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessMove chessMove)) {
            return false;
        }
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) &&
                promotionPiece == chessMove.promotionPiece;
    }

    /**
     * equality and hashcode
     * updated 1/27/26 from p0 implementation
     */
    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
