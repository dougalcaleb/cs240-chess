package core;

import chess.ChessGame;
import clientmodel.RgbColor;
import repl.BaseRepl;
import websocket.messages.GameMoveMessage;
import websocket.messages.LegalMovesMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class WsMessageHandler {
    public static RgbColor notifColor = new RgbColor(1, 90, 138);

    public static void logMessage(ServerMessage message)
    {
        System.out.print(
            "\n" + getColorEsc(false, notifColor.red(), notifColor.green(), notifColor.blue()) + SET_TEXT_COLOR_WHITE + " > " +
            message.getServerMessageContent() +
            " " + RESET_TEXT_COLOR + RESET_BG_COLOR
        );
        BaseRepl.printPrompt();
    }

    public static void handleGameMove(GameMoveMessage message)
    {
        if (message.getServerMessageContent() != null && !message.getServerMessageContent().isEmpty())
        {
            logMessage(message);
        }
        BaseRepl.game = message.gameData.game;
        BaseRepl.gameName = message.gameData.gameName;
        if (BaseRepl.color == null)
        {
            System.out.print("\n" + BaseRepl.printChessboard(ChessGame.TeamColor.WHITE) + "\n");
        }
        else
        {
            System.out.print("\n" + BaseRepl.printChessboard() + "\n");
        }

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
