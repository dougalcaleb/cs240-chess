package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    public ChessMove move;
    public String username;
    public ChessGame.TeamColor color;

    public MakeMoveCommand(String authToken, Integer gameID, String username, ChessGame.TeamColor color, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.username = username;
        this.color = color;
    }
}
