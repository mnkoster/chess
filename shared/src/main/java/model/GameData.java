package model;

import chess.ChessGame;

/**
 * GameData record
 * added 2/28/26 for phase 3
 * @param gameID
 * @param whiteUsername
 * @param blackUsername
 * @param gameName
 * @param game
 * @param isGameOver
 */
public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game,
        boolean isGameOver
) {}
