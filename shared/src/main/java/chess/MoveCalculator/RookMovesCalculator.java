package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

import java.util.List;

public class RookMovesCalculator extends PieceMovesCalculator
{
    public RookMovesCalculator(ChessBoard board, ChessPosition position, TeamColor teamColor) {
        super(board, position, PieceType.ROOK, teamColor);
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
