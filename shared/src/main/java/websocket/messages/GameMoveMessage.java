package websocket.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import sharedmodel.GameData;

public class GameMoveMessage extends ServerMessage {
    @Expose
    public GameData game;

    public GameMoveMessage(String message, GameData gameData) {
        super(ServerMessageType.LOAD_GAME, message);
        this.game = gameData;
    }

    public GameData getGame(){
        return game;
    }

    public String serialize() {
        Gson serializer = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

        return serializer.toJson(this);
    }
}
