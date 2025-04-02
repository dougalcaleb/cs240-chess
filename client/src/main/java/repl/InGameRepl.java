package repl;

import chess.ChessMove;
import chess.ChessPosition;
import clientmodel.FacadeResult;
import core.ServerFacade;

import java.util.Map;

public class InGameRepl extends BaseRepl {
    public InGameRepl()
    {
        super();

        helpText = Map.of(
            "help", new String[] { "[string command?]",
                        "Displays available list of commands. Can display help about a specific command if provided" },
            "quit", new String[] { "", "Exits the program" },
            "logout", new String[] { "", "Logs out the current user" },
            "move", new String[] { "[string start] [string end]", "Makes a move in the game given a start and end position (ex. move a2 a3)" },
            "print", new String[] { "", "Prints the chessboard" },
            "leave", new String[] { "", "Leaves the current game" },
            "resign", new String[] { "[string confirm? (-y)]", "Resigns from the current match" },
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

        return switch (args[0])
        {
            case "leave":
                WsHandler.leaveGame();
                newRepl = new LoggedInRepl();
                yield "";
            case "resign":
                WsHandler.resignGame();
                newRepl = new LoggedInRepl();
                yield "";
            case "l":
            case "legal":
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
            case "move":
                if (commandArgs.length != 2)
                {
                    throw new RuntimeException("Invalid arguments: expected 2, got " + commandArgs.length);
                }
                WsHandler.makeMove(constructMove(commandArgs[0], commandArgs[1]));
                yield "";
            default: yield printHelpText();
        };
    }

    @Override
    public String getPrompt() {
        return "\n LOGGED IN [" + BaseRepl.username + "] [" + BaseRepl.gameName + "] > ";
    }

    private ChessMove constructMove(String start, String end)
    {
        ChessPosition startPos = convertPosition(start);
        ChessPosition endPos = convertPosition(end);

        return new ChessMove(startPos, endPos);
    }

    private ChessPosition convertPosition(String position)
    {
        Map<String, Integer> cols = Map.of(
            "a", 1,
            "b", 2,
            "c", 3,
            "d", 4,
            "e", 5,
            "f", 6,
            "g", 7,
            "h", 8
        );

        String[] parts = position.split("");

        if (parts.length != 2)
        {
            throw new RuntimeException("Invalid position '" + position + "'. Position should be in the format [column letter][row number] with no other characters. (ex. 'a2')");
        }

        return new ChessPosition(Integer.parseInt(parts[1]), cols.get(parts[0]));
    }
}
