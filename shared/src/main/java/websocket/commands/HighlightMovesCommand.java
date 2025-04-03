package websocket.commands;

import chess.ChessPosition;

public class HighlightMovesCommand extends UserGameCommand {
    public ChessPosition pieceAtPosition;

    public HighlightMovesCommand(String authToken, Integer gameID, ChessPosition piecePosition) {
        super(CommandType.SHOW_MOVES, authToken, gameID);
        pieceAtPosition = piecePosition;
    }
}
