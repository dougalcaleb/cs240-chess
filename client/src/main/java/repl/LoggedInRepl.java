package repl;

import clientmodel.FacadeResult;
import core.ServerFacade;

import java.util.Map;

public class LoggedInRepl extends BaseRepl {

    public LoggedInRepl()
    {
        super();

        helpText = Map.of(
            "help", new String[] { "[string command?]",
                        "Displays available list of commands. Can display help about a specific command if provided" },
            "quit", new String[] { "", "Exits the program" },
            "logout", new String[] { "", "Logs out the current user" },
            "create", new String[] { "[string name]", "Creates a new game" },
            "list", new String[] { "", "Lists all games" },
            "join", new String[] { "[int gameID] [string color (WHITE|BLACK)]", "Lists all games" },
            "observe", new String[] { "[int gameID]", "Joins the audience of a game" }
        );
    }

    @Override
    public String getPrompt() {
        return "\n LOGGED IN [" + BaseRepl.username + "] > ";
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
            case "logout": {
                FacadeResult result = ServerFacade.logout();
                if (result.success())
                {
                    newRepl = new LoggedOutRepl();
                }
                yield INDENT + result.message();
            }
            case "create": {
                FacadeResult result = ServerFacade.create(commandArgs);
                yield INDENT + result.message();
            }
            case "list": {
                FacadeResult result = ServerFacade.list();
                yield result.message();
            }
            case "join": {
                FacadeResult result = ServerFacade.join(commandArgs);
                if (result.success())
                {
                    newRepl = new InGameRepl();
                    WsHandler.safeConnect();
                    WsHandler.joinGame();
//                    yield INDENT + result.message() + "\n" + printChessboard();
                    yield "";
                }
                else {
                    yield INDENT + result.message();
                }
            }
            case "observe": {
                verifyArgCount(commandArgs, 1);
                Integer idx = Integer.parseInt(commandArgs[0]) - 1;
                if (idx < 0 || idx >= BaseRepl.listedGames.size())
                {
                    throw new RuntimeException("Invalid game ID");
                }
                WsHandler.safeConnect();
                BaseRepl.observingGame = BaseRepl.listedGames.get(idx).gameID;
                BaseRepl.color = null;
                WsHandler.observeGame();
                newRepl = new ObservingRepl();
                yield "";
            }
            case "help":
                if (args.length > 1)
                {
                    yield printHelpText(args[1]);
                }
                yield printHelpText();
            case "quit":
                running = false;
                yield "";
            default: yield printHelpText();
        };
    }
}
