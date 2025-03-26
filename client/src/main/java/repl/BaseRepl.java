package repl;

import chess.ChessGame;
import chess.ChessPiece;
import model.rgbColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ui.EscapeSequences.*;

public abstract class BaseRepl {
    public boolean running = true;
    public static String authToken = null;
    public static String username = null;
    public static int gameId = -1;
    protected Map<String, String[]> helpText;
    protected BaseRepl newRepl = null;
    public static final String INDENT = "   ";
    public static ChessGame game;
    public static ChessGame.TeamColor color;

    public abstract String getPrompt();
    public abstract String evaluate(String[] args);

    public BaseRepl getActiveRepl() {
        return newRepl;
    }

    public void resetActiveRepl()
    {
        newRepl = null;
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
            return INDENT + "Invalid command '"+value+'"';
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

    public static void setGame(ChessGame game)
    {
        BaseRepl.game = game;
    }

    public static String printChessboard(ChessGame.TeamColor player)
    {
        ChessPiece[][] initialBoard = game.getBoard().getBoardAsArray();
        ChessPiece[][] finalBoard = new ChessPiece[8][8];
        String[] colNames = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };
        String[] rowNames = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };

        // as stored, black is on bottom but we'll flip if the player is white
        if (player == ChessGame.TeamColor.WHITE)
        {
            int idx = 7;
            for (ChessPiece[] row : initialBoard)
            {
                List<ChessPiece> reversed = Arrays.asList(row);
                Collections.reverse(reversed);
                finalBoard[idx] = reversed.toArray(new ChessPiece[8]);
                idx--;
            }

            List<String> revFile = Arrays.asList(rowNames);
            Collections.reverse(revFile);
            rowNames = revFile.toArray(new String[8]);
        }
        else
        {
            finalBoard = initialBoard;

            List<String> revRank = Arrays.asList(colNames);
            Collections.reverse(revRank);
            colNames = revRank.toArray(new String[8]);
        }

        StringBuilder boardRepr = new StringBuilder();

        boolean isLight = true;
        rgbColor lightBg = new rgbColor(152,153,135);
        rgbColor darkBg = new rgbColor(59, 77, 42);
        rgbColor lightFg = new rgbColor(230, 230, 230);
        rgbColor darkFg = new rgbColor(15, 15, 15);
        rgbColor borderBg = new rgbColor(0, 23, 7);
        rgbColor borderFg = new rgbColor(26, 71, 39);

        boardRepr.append(getColorEsc(false, borderBg.red(), borderBg.green(), borderBg.blue()));
        boardRepr.append(getColorEsc(true, borderFg.red(), borderFg.green(), borderFg.blue()));
        boardRepr.append(EM_SPACE);
        boardRepr.append("  ");

        for (String colName : colNames)
        {
            boardRepr.append(" ");
            boardRepr.append(colName);
            boardRepr.append(EM_SPACE);
        }

        boardRepr.append("  ");
        boardRepr.append(EM_SPACE);
        boardRepr.append(RESET_BG_COLOR);
        boardRepr.append(RESET_TEXT_COLOR);

        boardRepr.append("\n");

        int rowIdx = 0;
        for (ChessPiece[] row : finalBoard)
        {
            boardRepr.append(getColorEsc(false, borderBg.red(), borderBg.green(), borderBg.blue()));
            boardRepr.append(getColorEsc(true, borderFg.red(), borderFg.green(), borderFg.blue()));
            boardRepr.append(EM_SPACE);
            boardRepr.append(rowNames[rowIdx]);
            boardRepr.append(" ");

            for (ChessPiece piece : row)
            {
                rgbColor bg = isLight ? lightBg : darkBg;
                boardRepr.append(getColorEsc(false, bg.red(), bg.green(), bg.blue()));
                if (piece != null)
                {
                    rgbColor fg = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? lightFg : darkFg;

                    boardRepr.append(getColorEsc(true, fg.red(), fg.green(), fg.blue()));
                    boardRepr.append(getPieceStr(piece));
                }
                else
                {
                    boardRepr.append(EMPTY);
                }
                boardRepr.append(RESET_BG_COLOR);
                boardRepr.append(RESET_TEXT_COLOR);
                isLight = !isLight;
            }

            boardRepr.append(getColorEsc(false, borderBg.red(), borderBg.green(), borderBg.blue()));
            boardRepr.append(getColorEsc(true, borderFg.red(), borderFg.green(), borderFg.blue()));
            boardRepr.append(" ");
            boardRepr.append(rowNames[rowIdx++]);
            boardRepr.append(EM_SPACE);

            boardRepr.append(RESET_BG_COLOR);
            boardRepr.append(RESET_TEXT_COLOR);

            isLight = !isLight;
            boardRepr.append("\n");
        }

        boardRepr.append(getColorEsc(false, borderBg.red(), borderBg.green(), borderBg.blue()));
        boardRepr.append(getColorEsc(true, borderFg.red(), borderFg.green(), borderFg.blue()));
        boardRepr.append(EM_SPACE);
        boardRepr.append("  ");

        for (String colName : colNames)
        {
            boardRepr.append(" ");
            boardRepr.append(colName);
            boardRepr.append(EM_SPACE);
        }

        boardRepr.append("  ");
        boardRepr.append(EM_SPACE);

        boardRepr.append(RESET_BG_COLOR);
        boardRepr.append(RESET_TEXT_COLOR);

        boardRepr.append("\n");

        return boardRepr.toString();
    }

    private static String getPieceStr(ChessPiece piece)
    {
        Map<ChessPiece.PieceType, String> whitePieces = Map.of(
            ChessPiece.PieceType.KING, WHITE_KING,
            ChessPiece.PieceType.QUEEN, WHITE_QUEEN,
            ChessPiece.PieceType.BISHOP, WHITE_BISHOP,
            ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT,
            ChessPiece.PieceType.ROOK, WHITE_ROOK,
            ChessPiece.PieceType.PAWN, WHITE_PAWN
        );
        Map<ChessPiece.PieceType, String> blackPieces = Map.of(
            ChessPiece.PieceType.KING, BLACK_KING,
            ChessPiece.PieceType.QUEEN, BLACK_QUEEN,
            ChessPiece.PieceType.BISHOP, BLACK_BISHOP,
            ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT,
            ChessPiece.PieceType.ROOK, BLACK_ROOK,
            ChessPiece.PieceType.PAWN, BLACK_PAWN
        );

        return piece.getTeamColor() == ChessGame.TeamColor.WHITE
            ? whitePieces.get(piece.getPieceType())
            : blackPieces.get(piece.getPieceType());
    }

    public static String printChessboard()
    {
        return printChessboard(color);
    }

}
