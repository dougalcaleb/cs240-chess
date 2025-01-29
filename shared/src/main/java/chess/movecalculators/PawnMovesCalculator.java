package chess.movecalculators;

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

    public static boolean lastMoveWasPawnDouble = false;
    public static ChessMove pawnDoubleMove;

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

        int dir = (teamColor == topColor)
            ? -1
            : 1;
        int homeRow = (teamColor == topColor)
            ? 7
            : 2;

        ChessPosition oneAhead = new ChessPosition(currentPosition.getRow() + dir, currentPosition.getColumn());
        // initial move
        if (currentPosition.getRow() == homeRow && currentBoard.getPiece(oneAhead) == null)
        {
            addIfValidAndFree(oneAhead);
            addIfValidAndFree(new ChessPosition(homeRow + (dir*2), currentPosition.getColumn()));
        }
        else // moves past initial
        {
            addIfValidAndFree(oneAhead);
        }

        // capture
        ChessPosition frontL = new ChessPosition(currentPosition.getRow() + dir, currentPosition.getColumn() - 1);
        ChessPosition frontR = new ChessPosition(currentPosition.getRow() + dir, currentPosition.getColumn() + 1);

        if ( frontL.isValid() && currentBoard.getPiece(frontL) != null )
        {
            addIfValidAndNotOwnTeam(frontL);
        }
        if ( frontR.isValid() && currentBoard.getPiece(frontR) != null )
        {
            addIfValidAndNotOwnTeam(frontR);
        }

        // en passant
        if (
            PawnMovesCalculator.lastMoveWasPawnDouble &&
            PawnMovesCalculator.pawnDoubleMove.getEndPosition().getRow() == currentPosition.getRow()
        ) {
            if (PawnMovesCalculator.pawnDoubleMove.getEndPosition().getColumn() == currentPosition.getColumn() - 1)
            {
                addIfValidAndNotOwnTeam(new ChessPosition(currentPosition.getRow() + dir, currentPosition.getColumn() - 1));
            }
            if (PawnMovesCalculator.pawnDoubleMove.getEndPosition().getColumn() == currentPosition.getColumn() + 1)
            {
                addIfValidAndNotOwnTeam(new ChessPosition(currentPosition.getRow() + dir, currentPosition.getColumn() + 1));
            }
        }

        return getConverted();
    }
}
