package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class KnightMovesCalculator extends PieceMovesCalculator
{
    public KnightMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    @Override
    public List<ChessMove> getMoves()
    {
        int row = currentPosition.getRow();
        int col = currentPosition.getColumn();

        // upper half
        addIfValid(new ChessPosition(row - 2, col + 1));
        addIfValid(new ChessPosition(row - 2, col - 1));
        addIfValid(new ChessPosition(row - 1, col - 2));
        addIfValid(new ChessPosition(row - 1, col + 2));
        // lower half
        addIfValid(new ChessPosition(row + 2, col + 1));
        addIfValid(new ChessPosition(row + 2, col - 1));
        addIfValid(new ChessPosition(row + 1, col - 2));
        addIfValid(new ChessPosition(row + 1, col + 2));

        return convert(moves);
    }
}
