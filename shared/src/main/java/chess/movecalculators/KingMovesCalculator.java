package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

import java.util.List;

public class KingMovesCalculator extends PieceMovesCalculator
{
    public KingMovesCalculator(ChessBoard board, ChessPosition position, TeamColor teamColor) {
        super(board, position, PieceType.KING, teamColor);
    }

    @Override
    public List<ChessMove> getMoves() {
        int row = currentPosition.getRow();
        int col = currentPosition.getColumn();

        // cardinals
        addIfValidAndNotOwnTeam(new ChessPosition(row, col + 1));
        addIfValidAndNotOwnTeam(new ChessPosition(row + 1, col));
        addIfValidAndNotOwnTeam(new ChessPosition(row, col - 1));
        addIfValidAndNotOwnTeam(new ChessPosition(row - 1, col));
        // diagonals
        addIfValidAndNotOwnTeam(new ChessPosition(row + 1, col + 1));
        addIfValidAndNotOwnTeam(new ChessPosition(row + 1, col - 1));
        addIfValidAndNotOwnTeam(new ChessPosition(row - 1, col - 1));
        addIfValidAndNotOwnTeam(new ChessPosition(row - 1, col + 1));

        return convert(moves);
    }
}
