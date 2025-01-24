package chess;

import chess.movecalculators.*;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType type;
    private ChessPosition currentPosition;
    private ChessBoard board;

    public boolean hasBeenMoved = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type, ChessBoard parentBoard)
    {
        board = parentBoard;
        teamColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type)
    {
        board = null;
        teamColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece copySource)
    {
        this.teamColor = copySource.getTeamColor();
        this.type = copySource.getPieceType();
        this.currentPosition = copySource.getCurrentPosition();
        this.board = copySource.board;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public void setCurrentPosition(ChessPosition position)
    {
        this.currentPosition = position;
    }
    public ChessPosition getCurrentPosition()
    {
       return currentPosition;
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return type;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public List<ChessMove> pieceMoves() throws RuntimeException {
        if (board == null)
        {
            throw new RuntimeException("ChessPiece not initialized with a board");
        }
        return pieceMoves(null, null);
    }

    // alternate params
    public List<ChessMove> pieceMoves(ChessBoard board)
    {
        return pieceMoves(board, currentPosition);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public List<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (myPosition != null)
        {
            currentPosition = myPosition;
        }
        if (board == null) {
            board = this.board;
        } else {
            this.board = board;
        }

        PieceMovesCalculator calc = switch (type) {
            case PieceType.ROOK -> new RookMovesCalculator(board, currentPosition, teamColor);
            case PieceType.QUEEN -> new QueenMovesCalculator(board, currentPosition, teamColor);
            case PieceType.BISHOP -> new BishopMovesCalculator(board, currentPosition, teamColor);
            case PieceType.KNIGHT -> new KnightMovesCalculator(board, currentPosition, teamColor);
            case PieceType.KING -> new KingMovesCalculator(board, currentPosition, teamColor, hasBeenMoved);
            case PieceType.PAWN -> new PawnMovesCalculator(board, currentPosition, teamColor, ChessGame.TeamColor.BLACK);
            default -> throw new RuntimeException("Unrecognized piece type: " + type.toString());
        };

        return calc.getMoves();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }
}
