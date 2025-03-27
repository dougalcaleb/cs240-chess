package repl;

import chess.ChessGame;
import chess.ChessPiece;
import clientmodel.RgbColor;
import sharedmodel.GameData;

import java.util.*;

import static ui.EscapeSequences.*;

public abstract class BaseRepl {
    public boolean running = true;
    public static String authToken = null;
    public static String username = null;
    public static int gameId = -1;
    public static String gameName = "";
    protected Map<String, String[]> helpText;
    protected BaseRepl newRepl = null;
    public static final String INDENT = "   ";
    public static ChessGame game;
    public static ChessGame.TeamColor color;

    public static ArrayList<GameData> listedGames = new ArrayList<>();

    private static final RgbColor LIGHT_BG = new RgbColor(152,153,135);
    private static final RgbColor DARK_BG = new RgbColor(59, 77, 42);
    private static final RgbColor LIGHT_FG = new RgbColor(230, 230, 230);
    private static final RgbColor DARK_FG = new RgbColor(15, 15, 15);
    private static final RgbColor BORDER_BG = new RgbColor(0, 23, 7);
    private static final RgbColor BORDER_FG = new RgbColor(26, 71, 39);

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

    public static String printChessboard(ChessGame.TeamColor player, boolean flip)
    {
        ChessPiece[][] initialBoard = game.getBoard().getBoardAsArray();
        ChessPiece[][] finalBoard = new ChessPiece[8][8];
        String[] colNames = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };
        String[] rowNames = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };

        if (player == ChessGame.TeamColor.WHITE)
        {
            int idx = 7;
            for (ChessPiece[] row : initialBoard)
            {
                if (flip)
                {
                    List<ChessPiece> toReverse = Arrays.asList(row);
                    Collections.reverse(toReverse);
                    finalBoard[idx] = toReverse.toArray(new ChessPiece[8]);
                }
                else
                {
                    finalBoard[idx] = row;
                }
                idx--;
            }

            List<String> revFile = Arrays.asList(rowNames);
            Collections.reverse(revFile);
            rowNames = revFile.toArray(new String[8]);
        }
        else
        {
            int idx = 0;
            for (ChessPiece[] row : initialBoard)
            {
                if (flip)
                {
                    finalBoard[idx] = row;
                }
                else
                {
                    List<ChessPiece> reversed = Arrays.asList(row);
                    Collections.reverse(reversed);
                    finalBoard[idx] = reversed.toArray(new ChessPiece[8]);
                }

                idx++;
            }

            List<String> revRank = Arrays.asList(colNames);
            Collections.reverse(revRank);
            colNames = revRank.toArray(new String[8]);
        }

        StringBuilder boardRepr = new StringBuilder();

        boardRepr.append(getColorEsc(false, BORDER_BG.red(), BORDER_BG.green(), BORDER_BG.blue()));
        boardRepr.append(getColorEsc(true, BORDER_FG.red(), BORDER_FG.green(), BORDER_FG.blue()));
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

        addChessboard(boardRepr, finalBoard, rowNames);


        boardRepr.append(getColorEsc(false, BORDER_BG.red(), BORDER_BG.green(), BORDER_BG.blue()));
        boardRepr.append(getColorEsc(true, BORDER_FG.red(), BORDER_FG.green(), BORDER_FG.blue()));
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

    private static void addChessboard(StringBuilder boardRepr, ChessPiece[][] finalBoard, String[] rowNames)
    {
        int rowIdx = 0;
        boolean isLight = true;
        for (ChessPiece[] row : finalBoard)
        {
            boardRepr.append(getColorEsc(false, BORDER_BG.red(), BORDER_BG.green(), BORDER_BG.blue()));
            boardRepr.append(getColorEsc(true, BORDER_FG.red(), BORDER_FG.green(), BORDER_FG.blue()));
            boardRepr.append(EM_SPACE);
            boardRepr.append(rowNames[rowIdx]);
            boardRepr.append(" ");

            for (ChessPiece piece : row)
            {
                RgbColor bg = isLight ? LIGHT_BG : DARK_BG;
                boardRepr.append(getColorEsc(false, bg.red(), bg.green(), bg.blue()));
                if (piece != null)
                {
                    RgbColor fg = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? LIGHT_FG : DARK_FG;

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

            boardRepr.append(getColorEsc(false, BORDER_BG.red(), BORDER_BG.green(), BORDER_BG.blue()));
            boardRepr.append(getColorEsc(true, BORDER_FG.red(), BORDER_FG.green(), BORDER_FG.blue()));
            boardRepr.append(" ");
            boardRepr.append(rowNames[rowIdx++]);
            boardRepr.append(EM_SPACE);

            boardRepr.append(RESET_BG_COLOR);
            boardRepr.append(RESET_TEXT_COLOR);

            isLight = !isLight;
            boardRepr.append("\n");
        }
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
        return printChessboard(color, true);
    }

}
