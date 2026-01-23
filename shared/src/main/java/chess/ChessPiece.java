package chess;

import java.util.ArrayList;
import java.util.Collection;

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
            moves = straightMoves(board, piece, myPosition, 0); // straights
        }
        // KNIGHT
        if (piece.getPieceType() == PieceType.KNIGHT) {
            moves = knightMoves(board, piece, myPosition);
        }
        // BISHOP
        if (piece.getPieceType() == PieceType.BISHOP) {
            moves = straightMoves(board, piece, myPosition, 1); // diagonals
        }
        // QUEEN
        if (piece.getPieceType() == PieceType.QUEEN) {
            moves = straightMoves(board, piece, myPosition, 0); // straights
            moves.addAll(straightMoves(board, piece, myPosition, 1)); // diagonals
        }
        // KING
        if (piece.getPieceType() == PieceType.KING) {
            moves = kingMoves(board, piece, myPosition);
        }
        return moves; // added 1/20/26, phase 0 video UPDATE
    }

    // ***********************************************************************************
    // ******************************** PIECE MOVE ***************************************
    // ***********************************************************************************

    /**
     * Check if space is out of bounds before making move
     * added 1/23/26 for piece moves
     */
    private boolean inBound(int row, int col) { return (1 <= row && row <= 8 && 1 <= col && col <= 8); }

    /**
     * General check if position is open on the board
     * added 1/22/26 for piece moves
     */
    private boolean openPosition(ChessBoard board, ChessPosition otherPosition) {
        ChessPiece piece = board.getPiece(otherPosition);
        return piece == null;
    }

    /**
     * Check if unavailable space is enemy/capturable
     * added 1/22/26 for piece moves
     */
    private boolean capturable(ChessPiece piece, ChessPiece other) { return piece.pieceColor != other.pieceColor; }

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
        // check if space ahead up to maxSpaces is clear
        for (int i = 1; i < maxSpaces+1; i++) {
            int row_check = row + (i*direction);
            ChessPosition otherPosition = new ChessPosition(row_check, col);
            if (!openPosition(board, otherPosition)) { break; }// check if space is open
            if (row_check == 8 || row_check == 1) { // edge/promotion
                spaces.add(new ChessMove(myPosition, otherPosition, PieceType.QUEEN));
                spaces.add(new ChessMove(myPosition, otherPosition, PieceType.ROOK));
                spaces.add(new ChessMove(myPosition, otherPosition, PieceType.BISHOP));
                spaces.add(new ChessMove(myPosition, otherPosition, PieceType.KNIGHT));
            } else { spaces.add(new ChessMove(myPosition, otherPosition, null)); } // space ahead
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
        // check one space forward and left
        if (inBound(row_check, left_col)) {
            ChessPosition otherPosition = new ChessPosition(row_check, left_col);
            if (!openPosition(board, otherPosition)) { // check if space is open
                ChessPiece other = board.getPiece(otherPosition);
                if (capturable(piece, other)) {
                    if (row_check == 8 || row_check == 1) { // add all promotions
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.QUEEN));
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.ROOK));
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.BISHOP));
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.KNIGHT));
                    } else { captureSpace.add(new ChessMove(myPosition, otherPosition, null)); } // space ahead
                }
            }
        }
        // check one space forward and right
        if (inBound(row_check, right_col)) {
            ChessPosition otherPosition = new ChessPosition(row_check, right_col);
            if (!openPosition(board, otherPosition)) { // check if space is open
                ChessPiece other = board.getPiece(otherPosition);
                if (capturable(piece, other)) {
                    if (row_check == 8 || row_check == 1) { // edge/promotion
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.QUEEN));
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.ROOK));
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.BISHOP));
                        captureSpace.add(new ChessMove(myPosition, otherPosition, PieceType.KNIGHT));
                    } else { captureSpace.add(new ChessMove(myPosition, otherPosition, null)); } // space ahead
                }
            }
        }
        return captureSpace;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        Collection<ChessMove> spaces = new ArrayList<>();
        // row and column integers for reference
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (inBound(row + i, col + j)) {
                    ChessPosition otherPosition = new ChessPosition(row + i, col + j);
                    if (openPosition(board, otherPosition)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                    else {
                        ChessPiece other = board.getPiece(otherPosition);
                        if (capturable(piece, other)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                    }
                }
            }
        }
        return spaces;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        Collection<ChessMove> spaces = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        int[][] possible = {{start_row + 2, start_col - 1}, {start_row + 2, start_col + 1},
                {start_row - 2, start_col - 1}, {start_row - 2, start_col + 1},
                {start_row + 1, start_col - 2}, {start_row + 1, start_col + 2},
                {start_row - 1, start_col - 2}, {start_row - 1, start_col + 2}};
        for (int i=0; i < 8; i++) {
            if (inBound(possible[i][0], possible[i][1])) {
                ChessPosition otherPosition = new ChessPosition(possible[i][0], possible[i][1]);
                if (openPosition(board, otherPosition)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                else {
                    ChessPiece other = board.getPiece(otherPosition);
                    if (capturable(piece, other)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                }
            }
        }
        return spaces;
    }

    private Collection<ChessMove> straightMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition, int offset) {
        Collection<ChessMove> spaces = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        // offset 0: forward; offset 1: diagonal up right
        for (int i = 1; i < 8; i++) {
            int check_row = start_row + i; int check_col = start_col + (offset * i);
            if (!inBound(check_row, check_col)) { break; }
            ChessPosition otherPosition = new ChessPosition(check_row, check_col);
            if (openPosition(board, otherPosition)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
            else {
                ChessPiece other = board.getPiece(otherPosition);
                if (capturable(piece, other)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                break;
            }
        }
        // offset 0: backward; offset 1: diagonal back left
        for (int i = 1; i < 8; i++) {
            int check_row = start_row - i; int check_col = start_col - (offset * i);
            if (!inBound(check_row, check_col)) {
                break;
            }
            ChessPosition otherPosition = new ChessPosition(check_row, check_col);
            if (openPosition(board, otherPosition)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
            else {
                ChessPiece other = board.getPiece(otherPosition);
                if (capturable(piece, other)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                break;
            }
        }
        // offset 0: right; offset 1: diagonal back right
        for (int i = 1; i < 8; i++) {
            int check_row = start_row - (offset * i); int check_col = start_col + i;
            if (!inBound(check_row, check_col)) {
                break;
            }
            ChessPosition otherPosition = new ChessPosition(check_row, check_col);
            if (openPosition(board, otherPosition)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
            else {
                ChessPiece other = board.getPiece(otherPosition);
                if (capturable(piece, other)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                break;
            }
        }
        // offset 0: left; offset 1: diagonal up left
        for (int i = 1; i < 8; i++) {
            int check_row = start_row + (offset * i); int check_col = start_col - i;
            if (!inBound(check_row, check_col)) {
                break;
            }
            ChessPosition otherPosition = new ChessPosition(check_row, check_col);
            if (openPosition(board, otherPosition)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
            else {
                ChessPiece other = board.getPiece(otherPosition);
                if (capturable(piece, other)) { spaces.add(new ChessMove(myPosition, otherPosition, null)); }
                break;
            }
        }
        return spaces;
    }

    // ***********************************************************************************
    // ******************************** EQUAL/HASH ***************************************
    // ***********************************************************************************

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
