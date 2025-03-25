package repl;

import java.util.Map;

public abstract class BaseRepl {
    public boolean running = true;
    protected static String authToken = null;
    protected static String username = null;
    protected Map<String, String[]> helpText;
    protected BaseRepl newRepl = null;
    protected final String INDENT = "   ";

    public abstract String getPrompt();
    public abstract String evaluate(String[] args);
    public BaseRepl getActiveRepl() {
        return newRepl;
    }

    protected String printHelpText()
    {
        StringBuilder output = new StringBuilder();

        for (var pair : helpText.entrySet())
        {
            if (!output.isEmpty())
            {
                output.append("\n");
            }
            output.append(INDENT);
            output.append(pair.getKey());
            if (!pair.getValue()[0].isEmpty())
            {
                output.append(" ");
            }
            output.append(pair.getValue()[0]);
            output.append(" - ");
            output.append(pair.getValue()[1]);
        }

        return output.toString();
    }

    protected String printHelpText(String value)
    {
        if (!helpText.containsKey(value))
        {
            return "Invalid command '"+value+'"';
        }

        StringBuilder output = new StringBuilder();

        output.append(INDENT);
        output.append(value);
        if (!helpText.get(value)[0].isEmpty())
        {
            output.append(" ");
        }
        output.append(helpText.get(value)[0]);
        output.append(" - ");
        output.append(helpText.get(value)[1]);

        return output.toString();
    }

    public static void setAuthToken(String token)
    {
        authToken = token;
    }

    public static void setUsername(String username)
    {
        BaseRepl.username = username;
    }

}
