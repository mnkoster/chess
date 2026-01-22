package chess;

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
     * equals override
     * added 1/21/26 to pass EqualsTestingUtility (generated)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // same object
        if (o == null || getClass() != o.getClass()) return false; // not a ChessMove

        ChessMove that = (ChessMove) o;

        // Compare start and end positions using their equals
        if (!startPosition.equals(that.startPosition)) return false;
        if (!endPosition.equals(that.endPosition)) return false;

        // Compare promotionPiece (can be null)
        if (promotionPiece != that.promotionPiece) return false;

        return true;
    }

    /**
     * hashcode override
     * added 1/21/26 to pass EqualsTestingUtility (generated)
     */
    @Override
    public int hashCode() {
        int result = startPosition.hashCode();
        result = 31 * result + endPosition.hashCode();
        result = 31 * result + (promotionPiece == null ? 0 : promotionPiece.hashCode());
        return result;
    }
}
