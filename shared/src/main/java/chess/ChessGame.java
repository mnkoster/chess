package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard currBoard;
    private TeamColor turn;

    // Extra Credit Variable - En Passant
    private ChessPosition enPassantTarget;

    public ChessGame() {
        currBoard = new ChessBoard();
        currBoard.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return turn; }

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
     * helper function to get opposing color
     * added 2/4/26 for p1 implementation
     */
    private TeamColor opponentColor(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;
    }

    /**
     * Square is under attack
     * added 2/6/26 for castling
     */
    private boolean squareUnderAttack(ChessPosition pos, TeamColor byTeam) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Collection<ChessMove> moves = getMoves(i, j, byTeam);
                if (moves == null) continue;

                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(pos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     * added 2/3/26 for phase 1 implementation
     * updated 2/4/26 for phase 1 implementation
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = currBoard.getPiece(startPosition);
        if (currPiece == null) { return null; }

        Collection<ChessMove> validatedMoves = new ArrayList<>();
        Collection<ChessMove> unfilteredMoves = currPiece.pieceMoves(currBoard, startPosition);

        for (ChessMove move : unfilteredMoves) {
            if (movePutsKingInCheck(startPosition, move, currPiece, currPiece.getTeamColor())) {
                continue;
            }
            validatedMoves.add(move);
        }

        // En passant
        if (enPassantTarget != null) {
            ChessPosition target = enPassantTarget;
            // Pawn must be next to the en passant pawn
            if (Math.abs(startPosition.getColumn() - target.getColumn()) == 1) {
                validatedMoves.add(new ChessMove(startPosition, target, null));
            }
        }

        // Castling
        if (currPiece.getPieceType() == ChessPiece.PieceType.KING && currPiece.getPieceMoved()) {
            TeamColor team = currPiece.getTeamColor();
            TeamColor opponent = opponentColor(team);
            int row = (team == TeamColor.WHITE) ? 1 : 8;

            // King must not be in check
            if (!isInCheck(team)) {

                // ======================
                // Kingside castling
                // ======================
                ChessPosition rookPosK = new ChessPosition(row, 8);
                ChessPiece rookK = currBoard.getPiece(rookPosK);

                if (rookK != null &&
                        rookK.getPieceType() == ChessPiece.PieceType.ROOK &&
                        rookK.getPieceMoved()) {

                    ChessPosition f = new ChessPosition(row, 6);
                    ChessPosition g = new ChessPosition(row, 7);

                    if (currBoard.getPiece(f) == null &&
                            currBoard.getPiece(g) == null &&
                            squareUnderAttack(f, opponent) &&
                            squareUnderAttack(g, opponent)) {

                        validatedMoves.add(
                                new ChessMove(startPosition, g, null)
                        );
                    }
                }

                // ======================
                // Queenside castling
                // ======================
                ChessPosition rookPosQ = new ChessPosition(row, 1);
                ChessPiece rookQ = currBoard.getPiece(rookPosQ);

                if (rookQ != null &&
                        rookQ.getPieceType() == ChessPiece.PieceType.ROOK &&
                        rookQ.getPieceMoved()) {
                    ChessPosition d = new ChessPosition(row, 4);
                    ChessPosition c = new ChessPosition(row, 3);
                    ChessPosition b = new ChessPosition(row, 2);

                    if (currBoard.getPiece(d) == null &&
                            currBoard.getPiece(c) == null &&
                            currBoard.getPiece(b) == null &&
                            squareUnderAttack(d, opponent) &&
                            squareUnderAttack(c, opponent)) {

                        validatedMoves.add(
                                new ChessMove(startPosition, c, null)
                        );
                    }
                }
            }
        }
        return validatedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     * added 2/3/26 for phase 1 implementation
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
        if (validMoves.isEmpty()) {
            throw new InvalidMoveException("No valid moves");
        }
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Not valid move!");
        }

        // Piece exists and is valid move; make move
        ChessPiece.PieceType promoType = move.getPromotionPiece();
        if (promoType != null) {
            piece = new ChessPiece(piece.getTeamColor(), promoType);
        }

        // Handle enpassant updates
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                endPos.equals(enPassantTarget)) {

            // Captured pawn is behind the target square
            int capturedRow = (piece.getTeamColor() == TeamColor.WHITE)
                    ? endPos.getRow() - 1
                    : endPos.getRow() + 1;

            ChessPosition capturedPawnPos =
                    new ChessPosition(capturedRow, endPos.getColumn());
            currBoard.removePiece(capturedPawnPos);
        }

        // Castling updates
        if (piece.getPieceType() == ChessPiece.PieceType.KING || piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            piece.setPieceMoved(true);
        }

        // Phase 1 existing implementation
        currBoard.removePiece(startPos);
        currBoard.addPiece(endPos, piece);

        // Update team turn
        TeamColor opposingColor = opponentColor(currColor);
        setTeamTurn(opposingColor);

        // Detect en passant for opposing team
        enPassantTarget = null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int startRow = startPos.getRow();
            int endRow = endPos.getRow();

            // Pawn moved two squares
            if (Math.abs(startRow - endRow) == 2) {
                int passedRow = (startRow + endRow) / 2;
                enPassantTarget = new ChessPosition(passedRow, startPos.getColumn());
            }
        }

        // Castling moving rook
        if (piece.getPieceType() == ChessPiece.PieceType.KING &&
                Math.abs(startPos.getColumn() - endPos.getColumn()) == 2) {

            int row = startPos.getRow();

            if (endPos.getColumn() == 7) { // Kingside
                ChessPosition rookStart = new ChessPosition(row, 8);
                ChessPosition rookEnd = new ChessPosition(row, 6);

                ChessPiece rook = currBoard.getPiece(rookStart);
                currBoard.removePiece(rookStart);
                currBoard.addPiece(rookEnd, rook);
                rook.setPieceMoved(true);

            } else { // Queenside
                ChessPosition rookStart = new ChessPosition(row, 1);
                ChessPosition rookEnd = new ChessPosition(row, 4);

                ChessPiece rook = currBoard.getPiece(rookStart);
                currBoard.removePiece(rookStart);
                currBoard.addPiece(rookEnd, rook);
                rook.setPieceMoved(true);
            }
        }
    }

    /**
     * Helper function to get piece moves
     * added 2/4/26 for p1 implementation
     */
    private Collection<ChessMove> getMoves(int row, int col, TeamColor tryColor) {
        ChessPosition tryPos = new ChessPosition(row, col);
        ChessPiece piece = currBoard.getPiece(tryPos);

        if (piece != null && piece.getTeamColor() == tryColor) {
            return piece.pieceMoves(currBoard, tryPos);
        }
        return null;
    }

    /**
     * Helper function to simulate move
     * added 2/4/26 for p1 implementation
     */
    private boolean movePutsKingInCheck(ChessPosition startPos, ChessMove move, ChessPiece piece, TeamColor teamColor) {
        ChessPosition endPos = move.getEndPosition();
        ChessPiece captured = currBoard.getPiece(endPos);
        // simulate move
        currBoard.removePiece(startPos);
        currBoard.addPiece(endPos, piece);
        boolean inCheck = isInCheck(teamColor);
        // undo move
        currBoard.removePiece(endPos);
        currBoard.addPiece(startPos, piece);
        if (captured != null) {
            currBoard.addPiece(endPos, captured);
        }

        // return if it found an escape move
        return inCheck;
    }

    /**
     * Helper function to find teamColor king
     * added 2/4/26 for p1 implementation
     */
    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition tryPos = new ChessPosition(i, j);
                ChessPiece tryPiece = currBoard.getPiece(tryPos);

                if (tryPiece != null &&
                        tryPiece.getTeamColor() == teamColor &&
                        tryPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return tryPos;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     * added 2/4/26 for p1 implementation
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingColor = opponentColor(teamColor);
        ChessPosition kingPos = findKing(teamColor);

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Collection<ChessMove> moves = getMoves(i, j, opposingColor);
                if (moves == null) {
                    continue;
                }
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     * added 2/4/26 for p1 implementation
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // return false if the king isn't in check
        if (!isInCheck(teamColor)) {
            return false;
        }

        // see if any moves could put the king out of check
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Collection<ChessMove> moves = getMoves(i, j, teamColor);
                if (moves == null) { continue; }
                ChessPosition startPos = new ChessPosition(i, j);
                ChessPiece piece = currBoard.getPiece(startPos);

                for (ChessMove move : moves) {
                    // found an escape move
                    if (!movePutsKingInCheck(startPos, move, piece, teamColor)) {
                       return false;
                    }
                }
            }
        }
        // is checkmate if the function hasn't returned above
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     * added 2/3/26 for p1 implementation - TO UPDATE
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> availableMoves = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition tryPos = new ChessPosition(i, j);
                ChessPiece piece = currBoard.getPiece(tryPos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> posMoves = validMoves(tryPos);
                    if (posMoves == null) { continue; }
                    availableMoves.addAll(posMoves);
                }
            }
        }
        return (availableMoves.isEmpty());
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     * added 2/3/26 for p1 implementation
     */
    public void setBoard(ChessBoard board) {
        this.currBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     * added 2/3/26 for p1 implementation
     */
    public ChessBoard getBoard() {
        return currBoard;
    }
}
