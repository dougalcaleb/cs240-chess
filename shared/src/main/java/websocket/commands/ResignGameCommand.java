package websocket.commands;

import chess.ChessGame;

public class ResignGameCommand extends UserGameCommand {
    public ChessGame.TeamColor color;
    public String username;

    public ResignGameCommand(String authToken, Integer gameID, String username, ChessGame.TeamColor color) {
        super(CommandType.RESIGN, authToken, gameID);
        this.color = color;
        this.username = username;
    }
}
