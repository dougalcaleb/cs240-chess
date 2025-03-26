import chess.ChessGame;
import chess.ChessPiece;
import core.ServerFacade;
import repl.BaseRepl;
import repl.LoggedOutRepl;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Main {
    public static String serverURL = "http://localhost:8080";

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ServerFacade.setServerURL(serverURL);

        BaseRepl repl = new LoggedOutRepl();
        Scanner input = new Scanner(System.in);

        while (repl.running)
        {

            try
            {
                System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_GREEN + repl.getPrompt() + RESET_TEXT_COLOR + RESET_TEXT_ITALIC);
                String result = repl.evaluate(input.nextLine().split(" +"));

                if (repl.getActiveRepl() != null)
                {
                    repl = repl.getActiveRepl();
                    // probably not necessary because garbage collection but...
                    repl.resetActiveRepl();
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