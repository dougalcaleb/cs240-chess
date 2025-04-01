package websocket.messages;

public class JoinedGameMessage extends ServerMessage {

    public JoinedGameMessage(ServerMessageType type, String message) {
        super(type, message);
    }
}
