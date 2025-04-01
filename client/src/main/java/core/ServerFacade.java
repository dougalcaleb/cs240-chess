package core;

import chess.ChessGame;
import clientmodel.*;
import com.google.gson.Gson;
import exception.RequestError;
import repl.BaseRepl;
import sharedmodel.GameData;
import sharedmodel.ListGamesResult;
import sharedmodel.LoginResult;
import sharedmodel.RegisterResult;

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
        serverURL = "http://" + url;
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
                case 401 -> finalResultMessage = "Unauthorized";
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
            int idColWidth = 8;

            BaseRepl.listedGames.clear();

            for (GameData game : response.games)
            {
                BaseRepl.listedGames.add(game);
                if (game.gameName.length() > nameColWidth)
                {
                    nameColWidth = game.gameName.length();
                }
            }

            gameListBuilder.append(SET_TEXT_BOLD);

            gameListBuilder.append(BaseRepl.INDENT);
            gameListBuilder.append("Game ID ");
            gameListBuilder.append("Name ");
            gameListBuilder.append(" ".repeat(Math.max(0, nameColWidth - 4)));
            gameListBuilder.append("Players");

            gameListBuilder.append(RESET_TEXT_BOLD_FAINT);

            int gameIdx = 1;
            for (GameData game : BaseRepl.listedGames)
            {
                gameListBuilder.append("\n");
                gameListBuilder.append(BaseRepl.INDENT);
                gameListBuilder.append(gameIdx);
                gameListBuilder.append(" ".repeat(Math.max(0, idColWidth - String.valueOf(gameIdx).length())));
                gameListBuilder.append(game.gameName);
                gameListBuilder.append(" ".repeat(Math.max(0, nameColWidth - game.gameName.length() + 1)));
                gameListBuilder.append("White: ");
                gameListBuilder.append(Objects.requireNonNullElse(game.whiteUsername, "    "));
                gameListBuilder.append(" ");
                gameListBuilder.append("Black: ");
                gameListBuilder.append(Objects.requireNonNullElse(game.blackUsername, "    "));

                gameIdx++;
            }

            finalResultMessage = gameListBuilder.toString();
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


    public static FacadeResult create(String[] args)
    {
        if (args.length != 1)
        {
            throw new RuntimeException("Invalid arguments: expected 1 argument, got " + args.length);
        }

        boolean finalResultSuccess = false;
        String finalResultMessage = "";

        try {
            httpRequest("/game", "POST", new NewGameRequest(args[0]), EmptyResult.class);
            finalResultMessage = "Successfully created game";
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
            int joinIdx = -1;

            try {
                joinIdx = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Please enter the Game ID (left column) instead of the game name");
            }

            if (BaseRepl.listedGames.isEmpty())
            {
                finalResultMessage = "No games found. Run 'list' to refresh the list of games or create a new game with 'create'.";
            }
            else
            {
                if (joinIdx < 1 || joinIdx > BaseRepl.listedGames.size())
                {
                    throw new RuntimeException("Invalid game ID. Must be between 1 and "+BaseRepl.listedGames.size()+", inclusive");
                }

                GameData joinAttempt = BaseRepl.listedGames.get(joinIdx - 1);

                httpRequest("/game", "PUT", new JoinGameRequest(args[1], joinAttempt.gameID), EmptyResult.class);

                BaseRepl.gameId = Integer.parseInt(args[0]);
                BaseRepl.trueGameId = joinAttempt.gameID;
                BaseRepl.color = ChessGame.TeamColor.valueOf(args[1]);
                BaseRepl.game = joinAttempt.game;
                BaseRepl.gameName = joinAttempt.gameName;

                finalResultMessage = "Successfully joined game";
                finalResultSuccess = true;
            }
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
