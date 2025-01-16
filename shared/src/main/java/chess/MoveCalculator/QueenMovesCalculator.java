package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class QueenMovesCalculator extends PieceMovesCalculator
{
    public QueenMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    @Override
    public List<ChessMove> getMoves()
    {
        // cardinals
        moves.addAll(straightLine(currentBoard, currentPosition, 1, 0));
        moves.addAll(straightLine(currentBoard, currentPosition, 0, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, 0));
        moves.addAll(straightLine(currentBoard, currentPosition, 0, -1));
        // diagonals
        moves.addAll(straightLine(currentBoard, currentPosition, 1, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, -1));
        moves.addAll(straightLine(currentBoard, currentPosition, 1, -1));

        return convert(moves);
    }
}
