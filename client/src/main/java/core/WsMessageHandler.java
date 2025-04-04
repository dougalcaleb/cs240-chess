package core;

import chess.ChessGame;
import clientmodel.RgbColor;
import repl.BaseRepl;
import websocket.messages.*;

import static ui.EscapeSequences.*;

public class WsMessageHandler {
    public static RgbColor notifColor = new RgbColor(1, 90, 138);
    public static RgbColor errorColor = new RgbColor(110, 13, 7);

    public static void logMessage(ServerMessage message)
    {
        if (message.getMessage() == null)
        {
            return;
        }

        System.out.print(
            "\n" + getColorEsc(false, notifColor.red(), notifColor.green(), notifColor.blue()) + SET_TEXT_COLOR_WHITE + " > " +
            message.getServerMessageContent() +
            " " + RESET_TEXT_COLOR + RESET_BG_COLOR
        );
        BaseRepl.printPrompt();
    }

    public static void logError(ServerErrorMessage message)
    {
        if (message.getErrorMessage() == null)
        {
            return;
        }

        System.out.print(
                "\n" + getColorEsc(false, errorColor.red(), errorColor.green(), errorColor.blue()) + SET_TEXT_COLOR_WHITE + " > " +
                        message.getErrorMessage() +
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
        BaseRepl.game = message.game.game;
        BaseRepl.gameName = message.game.gameName;
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

    public static void handleGameLoad(JoinedGameMessage message)
    {
        if (message.getServerMessageContent() != null && !message.getServerMessageContent().isEmpty())
        {
            logMessage(message);
        }
        BaseRepl.game = message.game.game;
        BaseRepl.gameName = message.game.gameName;
        if (BaseRepl.color == null) {
            System.out.print("\n" + BaseRepl.printChessboard(ChessGame.TeamColor.WHITE) + "\n");
        } else {
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
