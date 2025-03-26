package repl;

import core.ServerFacade;
import model.FacadeResult;

import java.util.Map;

public class LoggedOutRepl extends BaseRepl {

    public LoggedOutRepl()
    {
        super();

        helpText = Map.of(
            "help", new String[] { "[string command?]", "Displays available list of commands. Can display help about a specific command if provided" },
            "quit", new String[] { "", "Exits the program" },
            "register", new String[] { "[string username] [string password] [string email?]", "Registers a new user and logs in" },
            "login", new String[] { "[string username] [string password]", "Logs in an existing user" }
        );
    }

    public String evaluate(String[] args) {
        if (args.length == 0)
        {
            throw new RuntimeException("No arguments given");
        }

        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);

        return switch (args[0])
        {
            case "register": {
                FacadeResult result = ServerFacade.register(commandArgs);
                if (result.success())
                {
                    newRepl = new LoggedInRepl();
                }
                yield INDENT + result.message();
            }
            case "login": {
                FacadeResult result = ServerFacade.login(commandArgs);
                if (result.success())
                {
                    newRepl = new LoggedInRepl();
                }
                yield INDENT + result.message();
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

    public String getPrompt()
    {
        return "\n LOGGED OUT > ";
    }
}
