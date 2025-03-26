package chess;

import chess.movecalculators.KingInCheckCalculator;
import chess.movecalculators.PawnMovesCalculator;
import com.google.gson.annotations.Expose;

import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    @Expose
    public ChessBoard board;
    @Expose
    public TeamColor currentTurn;

    public ChessGame() {
        currentTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition)
    {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null)
        {
            return null;
        }

        List<ChessMove> allMoves = piece.pieceMoves(board);

        return KingInCheckCalculator.getSafeMoves(board, allMoves, piece.getTeamColor());
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        if (pieceToMove == null)
        {
            throw new InvalidMoveException("No piece at location");
        }

        if (pieceToMove.getTeamColor() != getTeamTurn())
        {
            throw new InvalidMoveException("Cannot move this piece, it is not their turn");
        }

        List<ChessMove> allMoves = pieceToMove.pieceMoves();

        boolean includedInPossibleMoves = false;

        for (ChessMove possibleMove : allMoves)
        {
            if (move.equals(possibleMove))
            {
                includedInPossibleMoves = true;
                break;
            }
        }

        if (!KingInCheckCalculator.isSafeMove(board, getTeamTurn(), move) || !includedInPossibleMoves)
        {
            throw new InvalidMoveException("Move is invalid");
        }

        if (PawnMovesCalculator.lastMoveWasPawnDouble)
        {
            move.enPassantCapture = PawnMovesCalculator.pawnDoubleMove.getEndPosition();
        }

        if (pieceToMove.getPieceType() == ChessPiece.PieceType.PAWN && move.isPawnDouble())
        {
            PawnMovesCalculator.lastMoveWasPawnDouble = true;
            PawnMovesCalculator.pawnDoubleMove = move;
        } else {
            PawnMovesCalculator.lastMoveWasPawnDouble = false;
            PawnMovesCalculator.pawnDoubleMove = null;
        }

        board.applyMove(move);
        setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return KingInCheckCalculator.isInCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return KingInCheckCalculator.isInCheck(board, teamColor) &&
                !KingInCheckCalculator.safeMovesExist(board, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !KingInCheckCalculator.isInCheck(board, teamColor) &&
                !KingInCheckCalculator.safeMovesExist(board, teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board)
    {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard()
    {
        return board;
    }
}
