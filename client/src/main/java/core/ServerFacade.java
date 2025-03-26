package core;

import chess.ChessGame;
import clientmodel.*;
import com.google.gson.Gson;
import exception.RequestError;
import sharedmodel.*;
import repl.BaseRepl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Objects;

import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_BOLD;

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

    public static FacadeResult list()
    {
        boolean finalResultSuccess = false;
        String finalResultMessage = "";

        try {
            ListGamesResult response = httpRequest("/game", "GET", null, ListGamesResult.class);
            StringBuilder gameListBuilder = new StringBuilder();

            int nameColWidth = 0;
            int idColWidth = 4;

            for (GameData game : response.games)
            {
                if (game.gameName.length() > nameColWidth)
                {
                    nameColWidth = game.gameName.length();
                }
            }

            gameListBuilder.append(SET_TEXT_BOLD);

            gameListBuilder.append(BaseRepl.INDENT);
            gameListBuilder.append("Name");
            gameListBuilder.append(" ".repeat(Math.max(0, nameColWidth - 4)));
            gameListBuilder.append("  ");
            gameListBuilder.append("ID  ");
            gameListBuilder.append("Players");

            gameListBuilder.append(RESET_TEXT_BOLD_FAINT);

            for (GameData game : response.games)
            {
                gameListBuilder.append("\n");
                gameListBuilder.append(BaseRepl.INDENT);
                gameListBuilder.append(game.gameName);
                gameListBuilder.append(" ".repeat(Math.max(0, nameColWidth - game.gameName.length())));
                gameListBuilder.append("  ");
                gameListBuilder.append(game.gameID);
                gameListBuilder.append(" ".repeat(Math.max(0, idColWidth - String.valueOf(game.gameID).length())));
                gameListBuilder.append("White: ");
                gameListBuilder.append(Objects.requireNonNullElse(game.whiteUsername, "    "));
                gameListBuilder.append(" ");
                gameListBuilder.append("Black: ");
                gameListBuilder.append(Objects.requireNonNullElse(game.blackUsername, "    "));
            }

            finalResultMessage = gameListBuilder.toString();
            finalResultSuccess = true;
        } catch (RequestError e) {
            switch (e.status)
            {
                default -> finalResultMessage = "Server error: " + e.getMessage();
            };
        }

        return new FacadeResult(finalResultSuccess, finalResultMessage);
    }


    public static FacadeResult create(String[] args)
    {
        if (args.length != 1)
        {
            throw new RuntimeException("Invalid arguments: expected 1 argument, got " + args.length);
        }

        boolean finalResultSuccess = false;
        String finalResultMessage = "";

        try {
            CreateGameResult response = httpRequest("/game", "POST", new NewGameRequest(args[0]), CreateGameResult.class);
            finalResultMessage = "Successfully created game. ID is " + response.gameID;
            finalResultSuccess = true;
        } catch (RequestError e) {
            switch (e.status)
            {
                default -> finalResultMessage = "Server error: " + e.getMessage();
            };
        }

        return new FacadeResult(finalResultSuccess, finalResultMessage);
    }

    public static FacadeResult logout()
    {
        boolean finalResultSuccess = false;
        String finalResultMessage = "";

        try {
            httpRequest("/session", "DELETE", null, EmptyResult.class);
            BaseRepl.setAuthToken(null);
            BaseRepl.setUsername(null);
            finalResultMessage = "Successfully logged out";
            finalResultSuccess = true;
        } catch (RequestError e) {
            switch (e.status)
            {
                case 401 -> finalResultMessage = "Unauthorized";
                default -> finalResultMessage = "Server error: " + e.getMessage();
            };
        }

        return new FacadeResult(finalResultSuccess, finalResultMessage);
    }

    public static FacadeResult join(String[] args)
    {
        if (args.length != 2)
        {
            throw new RuntimeException("Invalid arguments: expected 2 arguments, got " + args.length);
        }

        args[1] = args[1].toUpperCase();

        if (!args[1].equals("WHITE") && !args[1].equals("BLACK"))
        {
            throw new RuntimeException("Invalid arguments: argument [color] should match (WHITE|BLACK)");
        }

        boolean finalResultSuccess = false;
        String finalResultMessage = "";

        try {
            httpRequest("/game", "PUT", new JoinGameRequest(args[1], Integer.parseInt(args[0])), EmptyResult.class);
            ListGamesResult games = httpRequest("/game", "GET", null, ListGamesResult.class);
            GameData joining = null;

            for (GameData gameData : games.games)
            {
                if (gameData.gameID == Integer.parseInt(args[0]))
                {
                    joining = gameData;
                    break;
                }
            }

            if (joining == null)
            {
                throw new RuntimeException("Game not found");
            }

            BaseRepl.gameId = Integer.parseInt(args[0]);
            BaseRepl.color = ChessGame.TeamColor.valueOf(args[1]);
            BaseRepl.game = joining.game;

            finalResultMessage = "Successfully joined game";
            finalResultSuccess = true;
        } catch (RequestError e) {
            switch (e.status)
            {
                case 403 -> finalResultMessage = "Color already taken";
                case 400 -> finalResultMessage = "Game does not exist";
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

            if (BaseRepl.authToken != null)
            {
                connection.addRequestProperty("Authorization", BaseRepl.authToken);
            }

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
