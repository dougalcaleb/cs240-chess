package repl;

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
            "move", new String[] { "[string start] [string end]", "Logs out the current user" },
            "print", new String[] { "", "Prints the chessboard" }
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
            case "help":
                if (args.length > 1)
                {
                    yield printHelpText(args[1]);
                }
                yield printHelpText();
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
                yield INDENT + "Not implemented";
            default: yield printHelpText();
        };
    }

    @Override
    public String getPrompt() {
        return "\n LOGGED IN [" + BaseRepl.username + "] [" + BaseRepl.gameName + "] > ";
    }
}
