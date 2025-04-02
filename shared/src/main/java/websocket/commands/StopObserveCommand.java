package websocket.commands;

public class StopObserveCommand extends UserGameCommand {
    public String username;

    public StopObserveCommand(String authToken, Integer gameID, String username) {
        super(CommandType.STOP_OBSERVE, authToken, gameID);
        this.username = username;
    }
}
