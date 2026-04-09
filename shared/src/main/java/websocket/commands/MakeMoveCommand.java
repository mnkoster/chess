package websocket.commands;

import chess.ChessPiece;

public class MakeMoveCommand extends UserGameCommand {

    private final MoveDTO move;

    public MakeMoveCommand(CommandType commandType,
                           String authToken,
                           int gameID,
                           MoveDTO move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public MoveDTO getMove() {
        return move;
    }

    public static class MoveDTO {
        public Position start;
        public Position end;
        public ChessPiece.PieceType promoType;
    }

    public static class Position {
        public int row;
        public int column;
    }
}