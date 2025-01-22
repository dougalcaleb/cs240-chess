package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

import java.util.List;

public class QueenMovesCalculator extends PieceMovesCalculator
{
    public QueenMovesCalculator(ChessBoard board, ChessPosition position, TeamColor teamColor) {
        super(board, position, PieceType.QUEEN, teamColor);
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
