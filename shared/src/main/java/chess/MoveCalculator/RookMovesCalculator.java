package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class RookMovesCalculator extends PieceMovesCalculator
{
    public RookMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    @Override
    public List<ChessMove> getMoves()
    {
        moves.addAll(straightLine(currentBoard, currentPosition, 1, 0));
        moves.addAll(straightLine(currentBoard, currentPosition, 0, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, 0));
        moves.addAll(straightLine(currentBoard, currentPosition, 0, -1));

        return convert(moves);
    }
}
