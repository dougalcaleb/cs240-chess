package websocket.commands;

import chess.ChessGame;

public class LeaveGameCommand extends UserGameCommand {
    public ChessGame.TeamColor color;
    public String username;

    public LeaveGameCommand(String authToken, Integer gameID, String username, ChessGame.TeamColor color) {
        super(CommandType.LEAVE, authToken, gameID);
        this.color = color;
        this.username = username;
    }
}
