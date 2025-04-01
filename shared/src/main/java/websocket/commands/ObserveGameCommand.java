package websocket.commands;

public class ObserveGameCommand extends UserGameCommand {
    public String username;

    public ObserveGameCommand(String authToken, Integer gameID, String username) {
        super(CommandType.OBSERVE, authToken, gameID);
        this.username = username;
    }
}
