package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row; // added 1/20/26, phase 0 video
    private final int col; // added 1/20/26, phase 0 video

    public ChessPosition(int row, int col) {
        this.row = row; // added 1/20/26, phase 0 video
        this.col = col; // added 1/20/26, phase 0 video
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row; // added 1/20/26, phase 0 video
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col; // added 1/20/26, phase 0 video
    }

    /**
     * toString for readability in debugging
     * added 1/20/26, phase 0 video
     */
    @Override
    public String toString() {
        return String.format("[%d,%d]", row, col);
    }
}
