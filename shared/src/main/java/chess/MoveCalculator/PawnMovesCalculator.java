package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

import java.util.List;

public class PawnMovesCalculator extends PieceMovesCalculator
{
    private final TeamColor teamColor;
    private final TeamColor topColor;

    public PawnMovesCalculator(
            ChessBoard board,
            ChessPosition position,
            TeamColor teamColor,
            TeamColor topColor
    ) {
        super(board, position, PieceType.PAWN, teamColor);
        this.teamColor = teamColor;
        this.topColor = topColor;
    }

    @Override
    public List<ChessMove> getMoves() {
        int dir = 1; // movement direction, "forward"
        int homeRow = 2;

        if (teamColor == topColor) {
            dir = -1;
            homeRow = 6;
        }

        // initial move
        if (currentPosition.getRow() == homeRow)
        {
            addIfValidAndFree(new ChessPosition(homeRow + dir, currentPosition.getColumn()));
            addIfValidAndFree(new ChessPosition(homeRow + (dir*2), currentPosition.getColumn()));
        }
        else // moves past initial
        {
            addIfValidAndFree(new ChessPosition(homeRow + dir, currentPosition.getColumn()));
        }

        // capture
        ChessPosition frontL = new ChessPosition(currentPosition.getRow() + dir, currentPosition.getColumn() - 1);
        ChessPosition frontR = new ChessPosition(currentPosition.getRow() + dir, currentPosition.getColumn() + 1);

        if (frontL.isValid() && currentBoard.getPiece(frontL) != null)
        {
            moves.add(frontL);
        }
        if (frontR.isValid() && currentBoard.getPiece(frontR) != null)
        {
            moves.add(frontR);
        }

        return convert(moves);
    }
}
