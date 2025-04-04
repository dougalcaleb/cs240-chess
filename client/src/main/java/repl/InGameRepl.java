package repl;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import clientmodel.FacadeResult;
import core.ServerFacade;

import java.util.Map;

public class InGameRepl extends BaseRepl {
    private boolean confirmingResign = false;

    public InGameRepl()
    {
        super();

        helpText = Map.of(
            "help", new String[] { "[string command?]",
                        "Displays available list of commands. Can display help about a specific command if provided" },
            "quit", new String[] { "", "Exits the program" },
            "logout", new String[] { "", "Logs out the current user" },
            "move", new String[] {
                    "[string start] [string end] [string promotion? (QUEEN|ROOK|BISHOP|KNIGHT)",
                    "Makes a move in the game given a start and end position (ex. move a2 a3). If promoting a pawn, takes a third argument"
                },
            "print", new String[] { "", "Prints the chessboard" },
            "leave", new String[] { "", "Leaves the current game" },
            "resign", new String[] { "", "Resigns from the current match" },
            "legal", new String[] { "[string piece]", "Displays all legal moves for a piece at a given position (ex. legal h5)" }
        );
    }

    @Override
    public String evaluate(String[] args) {
        if (args.length == 0)
        {
            throw new RuntimeException("No arguments given");
        }

        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);

        if (confirmingResign)
        {
            if (args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y"))
            {
                confirmingResign = false;
                wsHandler.resignGame();
            }
            else
            {
                confirmingResign = false;
            }
            return "";
        }

        return switch (args[0])
        {
            case "leave":
                wsHandler.leaveGame();
                newRepl = new LoggedInRepl();
                yield "";
            case "resign":
                confirmingResign = true;
                yield "Are you sure you want to resign?";
            case "l":
            case "legal":
                verifyArgCount(commandArgs, 1);
                ChessPosition pos = convertPosition(commandArgs[0]);
                wsHandler.highlightMoves(pos);
                yield "";
            case "help":
                if (args.length > 1)
                {
                    yield printHelpText(args[1]);
                }
                yield printHelpText();
            case "p":
            case "print":
                yield printChessboard();
            case "quit":
                running = false;
                yield "";
            case "logout": {
                FacadeResult result = ServerFacade.logout();
                if (result.success())
                {
                    newRepl = new LoggedOutRepl();
                }
                yield INDENT + result.message();
            }
            case "m":
            case "move":
                if (commandArgs.length != 2 && commandArgs.length != 3)
                {
                    throw new RuntimeException("Invalid arguments: expected 2 or 3, got " + args.length);
                }
                if (commandArgs.length == 3)
                {
                    commandArgs[2] = commandArgs[2].toLowerCase();
                    if (
                        !commandArgs[2].equals("queen") && !commandArgs[2].equals("knight") &&
                        !commandArgs[2].equals("bishop") && !commandArgs[2].equals("rook")
                    ) {
                        throw new RuntimeException("Invalid promotion: 3rd argument must be 'QUEEN', 'ROOK', 'BISHOP' or  'KNIGHT'");
                    }
                    wsHandler.makeMove(constructMove(commandArgs[0], commandArgs[1], commandArgs[2]));
                } else {
                    wsHandler.makeMove(constructMove(commandArgs[0], commandArgs[1], null));
                }
                yield "";
            default: yield printHelpText();
        };
    }

    @Override
    public String getPrompt() {
        return "\n LOGGED IN [" + BaseRepl.username + "] [" + BaseRepl.gameName + "] > ";
    }

    private ChessMove constructMove(String start, String end, String promotion)
    {
        ChessPosition startPos = convertPosition(start);
        ChessPosition endPos = convertPosition(end);

        if (promotion != null)
        {
            return new ChessMove(startPos, endPos, ChessPiece.PieceType.valueOf(promotion.toUpperCase()));
        }
        return new ChessMove(startPos, endPos);
    }
}
