package core;

import com.google.gson.Gson;
import exception.RequestError;
import model.*;
import repl.BaseRepl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;

public class ServerFacade {

    private static String serverURL = "";

    public static void setServerURL(String url)
    {
        serverURL = url;
    }

    public static FacadeResult register(String[] args)
    {
        if (args.length < 2 || args.length > 3)
        {
            throw new RuntimeException("Invalid arguments: expected 2 or 3 arguments, got " + args.length);
        }

        boolean finalResultSuccess = false;
        String finalResultMessage = "";

        String email = args.length == 2
            ? null
            : args[2];

        try {
            RegisterResult response = httpRequest("/user", "POST", new RegisterRequest(args[0], args[1], email), RegisterResult.class);
            BaseRepl.setAuthToken(response.authToken);
            BaseRepl.setUsername(response.username);
            finalResultMessage = "Successfully registered and logged in as " + response.username;
            finalResultSuccess = true;
        } catch (RequestError e) {
            switch (e.status)
            {
                case 403 -> finalResultMessage = "Username '"+args[0]+"' is already taken.";
                default -> finalResultMessage = "Server error: " + e.getMessage();
            };
        }

        return new FacadeResult(finalResultSuccess, finalResultMessage);
    }

    public static FacadeResult login(String[] args)
    {
        if (args.length != 2)
        {
            throw new RuntimeException("Invalid arguments: expected 2 arguments, got " + args.length);
        }

        boolean finalResultSuccess = false;
        String finalResultMessage = "";

        try {
            LoginResult response = httpRequest("/session", "POST", new LoginRequest(args[0], args[1]), LoginResult.class);
            BaseRepl.setAuthToken(response.authToken);
            BaseRepl.setUsername(response.username);
            finalResultMessage = "Successfully logged in as " + response.username;
            finalResultSuccess = true;
        } catch (RequestError e) {
            switch (e.status)
            {
                case 401 -> finalResultMessage = "User '"+args[0]+"' does not exist.";
                default -> finalResultMessage = "Server error: " + e.getMessage();
            };
        }

        return new FacadeResult(finalResultSuccess, finalResultMessage);
    }




    private static <T> T httpRequest(String endpoint, String method, Object body, Class<T> responseSchema) throws RequestError
    {
        try
        {
            URI uri = new URI(serverURL + endpoint);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);

            if (body != null)
            {
                connection.addRequestProperty("Content-Type", "application/json");
                try (OutputStream toRequest = connection.getOutputStream())
                {
                    toRequest.write(new Gson().toJson(body).getBytes());
                }
            }

            connection.connect();

            int status = connection.getResponseCode();

            if (status != 200)
            {
                try (InputStream connectionError = connection.getErrorStream())
                {
                    HashMap data = new Gson().fromJson(new InputStreamReader(connectionError), HashMap.class);
                    throw new RequestError(status, data.get("message").toString());
                }
            }

            try (InputStream responseStream = connection.getInputStream())
            {
                InputStreamReader input = new InputStreamReader(responseStream);
                return new Gson().fromJson(input, responseSchema);
            }
        }
        catch (RequestError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.toString());
        }
    }
}
