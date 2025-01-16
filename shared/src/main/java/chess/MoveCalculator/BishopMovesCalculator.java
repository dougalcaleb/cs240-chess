package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class BishopMovesCalculator extends PieceMovesCalculator
{
    public BishopMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    @Override
    public List<ChessMove> getMoves()
    {
        moves.addAll(straightLine(currentBoard, currentPosition, 1, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, -1));
        moves.addAll(straightLine(currentBoard, currentPosition, 1, -1));

        return convert(moves);
    }
}
