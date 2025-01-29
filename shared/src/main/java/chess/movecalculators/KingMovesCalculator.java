package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class KingMovesCalculator extends PieceMovesCalculator
{
    private boolean hasMoved = false;
    private boolean checkCastle = true;

    public KingMovesCalculator(ChessBoard board, ChessPosition position, TeamColor teamColor, boolean hasMoved, boolean checkCastle) {
        super(board, position, PieceType.KING, teamColor);
        this.hasMoved = hasMoved;
        this.checkCastle = checkCastle;
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

        // king-side castle
        List<ChessMove> traversedKingSide = new ArrayList<>();
        traversedKingSide.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 5)));
        traversedKingSide.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 6)));
        traversedKingSide.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 7)));

        // It's not possible for an opponent's castle to capture the king when the active team is castling,
        // so to avoid recursion, don't check when already checking if castling is valid
        if (checkCastle)
        {
            if (
                currentBoard.getPiece(new ChessPosition(row, 8)) != null && // rook exists
                !hasMoved && !currentBoard.getPiece(new ChessPosition(row, 8)).hasBeenMoved && // neither king nor rook have moved
                currentBoard.getPiece(new ChessPosition(row, 6)) == null && currentBoard.getPiece(new ChessPosition(row, 7)) == null && // free space
                KingInCheckCalculator.getSafeMoves(currentBoard, traversedKingSide, teamColor, false).size() == 3 // all spaces are safe
            )
            {
                addIfValidAndFree(new ChessPosition(row, 7));
            }

            // queen-side castle
            List<ChessMove> traversedQueenSide = new ArrayList<>();
            traversedQueenSide.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 5)));
            traversedQueenSide.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 4)));
            traversedQueenSide.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 3)));

            if (
                currentBoard.getPiece(new ChessPosition(row, 1)) != null && // rook exists
                !hasMoved && !currentBoard.getPiece(new ChessPosition(row, 1)).hasBeenMoved && // neither king nor rook have moved
                currentBoard.getPiece(new ChessPosition(row, 4)) == null && currentBoard.getPiece(new ChessPosition(row, 3)) == null && // free space
                KingInCheckCalculator.getSafeMoves(currentBoard, traversedQueenSide, teamColor, false).size() == 3 // all spaces are safe
            )
            {
                addIfValidAndFree(new ChessPosition(row, 3));
            }
        }

        return getConverted();
    }
}
