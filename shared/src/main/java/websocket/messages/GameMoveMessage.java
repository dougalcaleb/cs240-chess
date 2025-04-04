package websocket.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import sharedmodel.GameData;

public class GameMoveMessage extends ServerMessage {
    @Expose
    public GameData gameData;

    public GameMoveMessage(String message, GameData gameData) {
        super(ServerMessageType.GAME_MOVE, message);
        this.gameData = gameData;
    }

    public String serialize() {
        Gson serializer = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

        return serializer.toJson(this);
    }
}
