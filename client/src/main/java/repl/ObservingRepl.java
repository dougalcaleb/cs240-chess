package repl;

import clientmodel.FacadeResult;
import core.ServerFacade;

import java.util.Map;

public class ObservingRepl extends BaseRepl {
    public ObservingRepl()
    {
        super();

        helpText = Map.of(
                "help", new String[] { "[string command?]",
                        "Displays available list of commands. Can display help about a specific command if provided" },
                "quit", new String[] { "", "Exits the program" },
                "logout", new String[] { "", "Logs out the current user" },
                "print", new String[] { "", "Prints the chessboard" },
                "leave", new String[] { "", "Stops observing the game" },
                "legal", new String[] { "[string piece]", "Displays all legal moves for a piece at a given position (ex. a6, h1)" }
        );
    }

    @Override
    public String getPrompt() {
        return "\n LOGGED IN [" + BaseRepl.username + "] [" + BaseRepl.gameName + "] (observing) > ";
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
            case "l":
            case "legal":
                yield "";
            case "leave":
                WsHandler.stopObserveGame();
                BaseRepl.observingGame = -1;
                newRepl = new LoggedInRepl();
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
            default: yield printHelpText();
        };
    }
}
