package core;

import repl.BaseRepl;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class WsMessageHandler {
    public static void logMessage(ServerMessage message)
    {
        System.out.print(
            "\n" + SET_BG_COLOR_BLUE + SET_TEXT_COLOR_WHITE + " > " +
            message.getServerMessageContent() +
            " " + RESET_TEXT_COLOR + RESET_BG_COLOR
        );
        BaseRepl.printPrompt();
    }
}
