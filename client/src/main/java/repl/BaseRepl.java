package repl;

import java.util.Map;

public abstract class BaseRepl {
    public boolean running = true;
    protected Map<String, String[]> helpText;

    public abstract String getPrompt();
    public abstract String evaluate(String[] args);

    protected String printHelpText()
    {
        StringBuilder output = new StringBuilder();

        for (var pair : helpText.entrySet())
        {
            output.append("\n");
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

        output.append("\n");
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

}
