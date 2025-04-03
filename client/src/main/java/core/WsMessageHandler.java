package core;

import repl.BaseRepl;
import websocket.messages.GameMoveMessage;
import websocket.messages.LegalMovesMessage;
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

    public static void handleGameMove(GameMoveMessage message)
    {
        logMessage(message);
        BaseRepl.game = message.gameData.game;
        System.out.print("\n" + BaseRepl.printChessboard() + "\n");
        BaseRepl.printPrompt();
    }

    public static void handleLegalMoves(LegalMovesMessage message)
    {
        if (message.positions == null)
        {
            logMessage(message);
        } else {
            System.out.print("\n" + BaseRepl.printChessboard(message.positions) + "\n");
        }
        BaseRepl.printPrompt();
    }
}
