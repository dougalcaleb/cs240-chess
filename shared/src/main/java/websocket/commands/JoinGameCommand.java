package websocket.commands;

import chess.ChessGame;

public class JoinGameCommand extends UserGameCommand {
    public ChessGame.TeamColor color;
    public String username;

    public JoinGameCommand(String authToken, Integer gameID, String username, ChessGame.TeamColor color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.color = color;
        this.username = username;
    }
}
