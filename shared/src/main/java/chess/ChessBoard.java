package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8]; // added 1/20/26, phase 0 video
    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece; // added 1/20/26, phase 0 video
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1]; // added 1/20/26, phase 0 video
    }

    /**
     * Sets up the back rank
     * Pass in color, row number
     * added 1/21/26 as helper function to reset board
     */
    public void backRank(ChessGame.TeamColor color, int row) {
        // ROOKS
        addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        // KNIGHTS
        addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        // BISHOPS
        addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        // ROYAL
        addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
    }

    /**
     * Sets up the pawn rank
     * Pass in color
     * added 1/21/26 as helper function to reset board
     */
    public void pawnRank(ChessGame.TeamColor color, int row) {
        // PAWNS
        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(row, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     * added 1/21/26 to reset board
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = null;
            }
        }
        backRank(ChessGame.TeamColor.WHITE, 1); // WHITE BACK RANK
        pawnRank(ChessGame.TeamColor.WHITE, 2); // WHITE PAWN RANK
        pawnRank(ChessGame.TeamColor.BLACK, 7); // BLACK PAWN RANK
        backRank(ChessGame.TeamColor.BLACK, 8); // BLACK BACK RANK
    }

    /**
     * equals override
     * checks that each piece on board is equivalent
     * added 1/21/26 to pass ChessBoardTests (generated)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessBoard other = (ChessBoard) o;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece thisPiece = this.squares[row][col];
                ChessPiece otherPiece = other.squares[row][col];

                if (thisPiece == null && otherPiece == null) {
                    continue;
                }
                if (thisPiece == null || otherPiece == null) {
                    return false;
                }
                if (!thisPiece.equals(otherPiece)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * hashcode override
     * added 1/21/26 to pass ChessBoardTests (generated)
     */
    @Override
    public int hashCode() {
        int result = 1;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                result = 31 * result + (squares[row][col] == null ? 0 : squares[row][col].hashCode());
            }
        }
        return result;
    }
}
