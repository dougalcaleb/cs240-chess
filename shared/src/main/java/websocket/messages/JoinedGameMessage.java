package websocket.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import sharedmodel.GameData;

public class JoinedGameMessage extends ServerMessage {
    @Expose
    public GameData game;

    public JoinedGameMessage(String message, GameData game) {
        super(ServerMessageType.LOAD_GAME, message);
        this.game = game;
    }

    public GameData getGame()
    {
        return game;
    }

//    public JoinedGameMessage

    public String serialize() {
        Gson serializer = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        return serializer.toJson(this);
    }
}
