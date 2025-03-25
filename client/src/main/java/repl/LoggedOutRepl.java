package repl;

import core.ServerFacade;

import java.util.Map;

import static ui.EscapeSequences.*;

public class LoggedOutRepl extends BaseRepl {

    private String loggedInAs = null;

    public LoggedOutRepl()
    {
        super();

        helpText = Map.of(
            "help", new String[] { "[command?]", "Displays available list of commands. Can display help about a specific command if provided" },
            "quit", new String[] { "", "Exits the program" },
            "register", new String[] { "[username] [password] [email?]", "Registers a new user and logs in" },
            "login", new String[] { "[username] [password]", "Logs in an existing user" }
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
            case "register":
                yield ServerFacade.register(commandArgs);
            case "login":
                yield "";
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
        return "\n\n" + RESET_TEXT_COLOR + SET_TEXT_FAINT + SET_TEXT_ITALIC + " LOGGED OUT >>> " + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT;
    }
}
