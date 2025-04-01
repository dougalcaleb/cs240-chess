import chess.ChessGame;
import chess.ChessPiece;
import core.ServerFacade;
import core.WebsocketHandler;
import repl.BaseRepl;
import repl.LoggedOutRepl;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class Main {
    public static String serverURL = "localhost:8080";

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        WebsocketHandler.serverURL = serverURL;
        ServerFacade.setServerURL(serverURL);

        BaseRepl.activeRepl = new LoggedOutRepl();
        Scanner input = new Scanner(System.in);

        while (BaseRepl.activeRepl.running)
        {

            try
            {
                BaseRepl.printPrompt();
                String result = BaseRepl.activeRepl.evaluate(input.nextLine().split(" +"));

                if (BaseRepl.activeRepl.getActiveRepl() != null)
                {
                    BaseRepl.activeRepl = BaseRepl.activeRepl.getActiveRepl();
                    // probably not necessary because garbage collection but...
                    BaseRepl.activeRepl.resetActiveRepl();
                }

                System.out.print(result);
            }
            catch (Exception e)
            {
                System.out.print( RESET_TEXT_COLOR + SET_TEXT_COLOR_RED + BaseRepl.INDENT + e.getMessage());
            }
        }
    }
}