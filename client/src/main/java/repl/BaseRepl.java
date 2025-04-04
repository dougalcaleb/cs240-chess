package repl;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import clientmodel.RgbColor;
import core.WebsocketHandler;
import sharedmodel.GameData;

import java.util.*;

import static ui.EscapeSequences.*;

public abstract class BaseRepl {
    public boolean running = true;
    public static String authToken = null;
    public static String username = null;
    public static int gameId = -1;
    public static int trueGameId = -1;
    public static int observingGame = -1;
    public static String gameName = "";
    protected Map<String, String[]> helpText;
    protected BaseRepl newRepl = null;
    public static final String INDENT = "   ";
    public static ChessGame game;
    public static ChessGame.TeamColor color = null;
    public static BaseRepl activeRepl;

    public static WebsocketHandler WsHandler = new WebsocketHandler();

    public static ArrayList<GameData> listedGames = new ArrayList<>();

    private static final RgbColor LIGHT_BG = new RgbColor(152,153,135);
    private static final RgbColor DARK_BG = new RgbColor(59, 77, 42);
    private static final RgbColor LIGHT_FG = new RgbColor(230, 230, 230);
    private static final RgbColor DARK_FG = new RgbColor(15, 15, 15);
    private static final RgbColor BORDER_BG = new RgbColor(0, 23, 7);
    private static final RgbColor BORDER_FG = new RgbColor(26, 71, 39);
    private static final RgbColor LIGHT_BG_HIGHLIGHT = new RgbColor(194, 199, 115);
    private static final RgbColor DARK_BG_HIGHLIGHT = new RgbColor(135, 158, 51);

    public abstract String getPrompt();
    public abstract String evaluate(String[] args);

    public BaseRepl getActiveRepl() {
        return newRepl;
    }

    public void resetActiveRepl()
    {
        newRepl = null;
    }

    public static void printPrompt()
    {
        System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_GREEN + BaseRepl.activeRepl.getPrompt() + RESET_TEXT_COLOR + RESET_TEXT_ITALIC);
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

    public static String printChessboard(ChessGame.TeamColor player, List<ChessPosition> highlighted)
    {
        ChessPiece[][] initialBoard = game.getBoard().getBoardAsArray();
        ChessPiece[][] printBase = new ChessPiece[8][8];
        String[] colNames = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };
        String[] rowNames = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };

        int initFlipIdx = 7;
        for (ChessPiece[] row : initialBoard)
        {
            printBase[initFlipIdx] = row.clone();
            initFlipIdx--;
        }



        if (player == ChessGame.TeamColor.WHITE)
        {
            List<String> revFile = Arrays.asList(rowNames.clone());
            Collections.reverse(revFile);
            rowNames = revFile.toArray(new String[8]);
        }
        else
        {
            int blackFlipVertIdx = 0;
            for (ChessPiece[] row : initialBoard)
            {
                List<ChessPiece> reversedRow = Arrays.asList(row.clone());
                Collections.reverse(reversedRow);
                printBase[blackFlipVertIdx] = reversedRow.toArray(new ChessPiece[8]);
                blackFlipVertIdx++;
            }

            List<String> revRank = Arrays.asList(colNames.clone());
            Collections.reverse(revRank);
            colNames = revRank.toArray(new String[8]);
        }

        ChessPiece[][] finalBoard = printBase.clone();

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

        addChessboard(boardRepr, finalBoard, rowNames, highlighted, player);


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

    private static void addChessboard(
            StringBuilder boardRepr,
            ChessPiece[][] finalBoard,
            String[] rowNames,
            List<ChessPosition> highlighted,
            ChessGame.TeamColor color
    )
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

            int colIdx = 0;
            for (ChessPiece piece : row)
            {
                ChessPosition renderPos = null;
                if (color == ChessGame.TeamColor.WHITE) {
                    renderPos = new ChessPosition(9 - (rowIdx + 1), colIdx + 1);
                } else {
                    renderPos = new ChessPosition(rowIdx + 1, 9 - (colIdx + 1));
                }

                RgbColor bg = isLight
                        ? highlighted.contains(renderPos)
                            ? LIGHT_BG_HIGHLIGHT
                            : LIGHT_BG
                        : highlighted.contains(renderPos)
                            ? DARK_BG_HIGHLIGHT
                            : DARK_BG;

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

                colIdx++;
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
        return printChessboard(color, new ArrayList<>());
    }

    public static String printChessboard(ChessGame.TeamColor homeColor)
    {
        return printChessboard(homeColor, new ArrayList<>());
    }

    public static String printChessboard(List<ChessPosition> highlightedSquares)
    {
        ChessGame.TeamColor printAs = ChessGame.TeamColor.WHITE;
        if (color != null)
        {
            printAs = color;
        }
        return printChessboard(printAs, highlightedSquares);
    }

    protected ChessPosition convertPosition(String position)
    {
        Map<String, Integer> cols = Map.of(
                "a", 1,
                "b", 2,
                "c", 3,
                "d", 4,
                "e", 5,
                "f", 6,
                "g", 7,
                "h", 8
        );

        String[] parts = position.split("");

        if (parts.length != 2)
        {
            throw new RuntimeException(
                "Invalid position '" + position +
                "'. Position should be in the format [column letter][row number] with no other characters. (ex. 'a2')"
            );
        }

        if (!cols.containsKey(parts[0]))
        {
            throw new RuntimeException("Invalid position '" + position + "'. Column name must be 'a' through 'h'.");
        }

        Integer parsedRow;

        try {
            parsedRow = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid position '" + position + "'. Row must be a number from 1 to 8 inclusive.");
        }

        if (parsedRow < 1 || parsedRow > 8)
        {
            throw new RuntimeException("Invalid position '" + position + "'. Row must be a number from 1 to 8 inclusive.");
        }

        return new ChessPosition(Integer.parseInt(parts[1]), cols.get(parts[0]));
    }

    protected void verifyArgCount(String[] args, int expected)
    {
        if (args.length != expected)
        {
            throw new RuntimeException("Invalid arguments: expected " + expected + ", got " + args.length);
        }
    }

}
