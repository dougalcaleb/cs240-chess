package sharedmodel;

import chess.ChessGame;

import java.util.Objects;

public class GameData {
    public int gameID;
    public String whiteUsername;
    public String blackUsername;
    public String gameName;
    public ChessGame game;

    public GameData(String name)
    {
        gameName = name;
        game = new ChessGame();
    }

    public GameData(int gameID, String wUsername, String bUsername, String name, ChessGame game)
    {
        this.gameID = gameID;
        whiteUsername = wUsername;
        blackUsername = bUsername;
        gameName = name;
        this.game = game;
    }

    public GameData(int gameID, String wUsername, String bUsername, String name)
    {
        this.gameID = gameID;
        whiteUsername = wUsername;
        blackUsername = bUsername;
        gameName = name;
    }

    public GameData() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID &&
                Objects.equals(whiteUsername, gameData.whiteUsername) &&
                Objects.equals(blackUsername, gameData.blackUsername) &&
                Objects.equals(gameName, gameData.gameName);
    }
}
