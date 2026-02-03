package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard currBoard = new ChessBoard();
    TeamColor turn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return Objects.equals(currBoard, chessGame.currBoard) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currBoard, turn);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     * added 2/3/26 for phase 1 implementation - TO UPDATE
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = currBoard.getPiece(startPosition);
        if (currPiece == null) { return null; }

        Collection<ChessMove> unfilteredMoves = currPiece.pieceMoves(currBoard, startPosition);
        return unfilteredMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     * added 2/3/26 for phase 1 implementation - TO UPDATE
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = currBoard.getPiece(startPos);
        TeamColor currColor = getTeamTurn();
        if ((piece == null) || (piece.getTeamColor() != currColor)) {
            throw new InvalidMoveException("Invalid position: empty or wrong color piece");
        }
        Collection<ChessMove> validMoves = validMoves(startPos);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Not valid move!");
        }

        // Piece exists and is valid move; make move
        ChessPiece.PieceType promoType = move.getPromotionPiece();
        if (promoType != null) {
            piece = new ChessPiece(piece.getTeamColor(), promoType);
        }
        currBoard.removePiece(startPos);
        currBoard.addPiece(endPos, piece);

        // Update team turn
        if (currColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     * added 2/3/26 for phase 1 implementation - TO UPDATE
     */
    public boolean isInCheck(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     * added 2/3/26 for phase 1 implementation - TO UPDATE
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     * added 2/3/26 for phase 1 implementation - TO UPDATE
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     * added 2/3/26 for phase 1 implementation
     */
    public void setBoard(ChessBoard board) {
        this.currBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     * added 2/3/26 for phase 1 implementation
     */
    public ChessBoard getBoard() {
        return currBoard;
    }
}
