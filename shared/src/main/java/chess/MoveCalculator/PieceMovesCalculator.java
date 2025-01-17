package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class PieceMovesCalculator
{
    protected final ChessBoard currentBoard;
    protected final ChessPosition currentPosition;
    protected List<ChessPosition> moves;
    protected final PieceType type;
    protected final TeamColor teamColor;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, PieceType type, TeamColor teamColor)
    {
        currentBoard = board;
        currentPosition = position;
        this.type = type;
        this.teamColor = teamColor;
    }

    // To be overridden
    public List<ChessMove> getMoves()
    {
        return null;
    }

    // Converts from a list of ChessPositions to ChessMoves
    protected List<ChessMove> convert(List<ChessPosition> positions)
    {
        List<ChessMove> moves = new ArrayList<>();

        for (ChessPosition pos : positions)
        {
            moves.add(new ChessMove(currentPosition, pos));
        }

        return moves;
    }

    // Get valid moves in a straight line as far as possible, including any captured piece at the end
    // Used for rook, queen, and bishop
    protected List<ChessPosition> straightLine(ChessBoard board, ChessPosition start, int xOffset, int yOffset)
    {
        List<ChessPosition> validSquares = new ArrayList<>();
        ChessPosition currentCheck = new ChessPosition(start.getRow() + yOffset, start.getColumn() + xOffset);

        // get empty squares
        while (currentCheck.isValid() && board.getPiece(currentCheck) == null)
        {
            validSquares.add(new ChessPosition(currentCheck));
            currentCheck = new ChessPosition(currentCheck.getRow() + xOffset, currentCheck.getColumn() + yOffset);
        }

        // get capturing square
        if (board.getPiece(currentCheck) != null && currentCheck.isValid())
        {
            validSquares.add(new ChessPosition(currentCheck));
        }

        return validSquares;
    }

    // Add the position if it is on the chessboard
    protected void addIfValid(ChessPosition position)
    {
        if (position.isValid()) {
            moves.add(position);
        }
    }

    // Add the position if it is on the chessboard and free of any other pieces
    protected void addIfValidAndFree(ChessPosition position)
    {
        if (position.isValid() && currentBoard.getPiece(position) == null)
        {
            moves.add(position);
        }
    }
}
