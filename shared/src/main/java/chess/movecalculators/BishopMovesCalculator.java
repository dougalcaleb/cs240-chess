package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

import java.util.List;

public class BishopMovesCalculator extends PieceMovesCalculator
{
    public BishopMovesCalculator(ChessBoard board, ChessPosition position, TeamColor teamColor) {
        super(board, position, PieceType.BISHOP, teamColor);
    }

    @Override
    public List<ChessMove> getMoves()
    {
        moves.addAll(straightLine(currentBoard, currentPosition, 1, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, 1));
        moves.addAll(straightLine(currentBoard, currentPosition, -1, -1));
        moves.addAll(straightLine(currentBoard, currentPosition, 1, -1));

        return getConverted();
    }
}
