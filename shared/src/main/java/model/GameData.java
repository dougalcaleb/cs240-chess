package model;

import chess.ChessGame;

public class GameData {
    public int gameID;
    public String whiteUsername;
    public String blackUsername;
    public String gameName;
    public ChessGame game;

    public GameData(String name)
    {
        gameName = name;
    }

    public GameData() {}
}
