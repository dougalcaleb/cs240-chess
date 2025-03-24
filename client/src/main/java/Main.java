import chess.ChessGame;
import chess.ChessPiece;
import repl.BaseRepl;
import repl.LoggedOutRepl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        BaseRepl repl = new LoggedOutRepl();
        Scanner input = new Scanner(System.in);

        while (repl.running)
        {
            System.out.print(repl.getPrompt());
            try
            {
                String result = repl.evaluate(input.nextLine().split(" +"));
                System.out.print(result);
            }
            catch (Exception e)
            {
                System.err.print(e.toString());
            }
        }
    }
}