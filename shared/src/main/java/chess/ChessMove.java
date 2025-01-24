package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPos;
    private final ChessPosition endPos;
    private final ChessPiece.PieceType promotion;

    public ChessMove( ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece )
    {
        startPos = startPosition;
        endPos = endPosition;
        promotion = promotionPiece;
    }

    public ChessMove( ChessPosition startPosition, ChessPosition endPosition )
    {
        startPos = startPosition;
        endPos = endPosition;
        promotion = null;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition()
    {
        return startPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition()
    {
       return endPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece()
    {
        return promotion;
    }

    // assumes that this is being called on a king's move
    public boolean isCastling()
    {
        return Math.abs(startPos.getColumn() - endPos.getColumn()) == 2;
    }

    public ChessMove getCastlingRookMove()
    {
        int startCol = (endPos.getColumn() - startPos.getColumn() > 0)
                ? 8
                : 1;
        int endCol = (endPos.getColumn() - startPos.getColumn() > 0)
                ? 6
                : 4;

        return new ChessMove(new ChessPosition(startPos.getRow(), startCol), new ChessPosition(startPos.getRow(), endCol));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPos, chessMove.startPos) && Objects.equals(endPos, chessMove.endPos) && promotion == chessMove.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, endPos, promotion);
    }
}
