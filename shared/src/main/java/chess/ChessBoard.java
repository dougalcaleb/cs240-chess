package chess;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;

    public ChessBoard()
    {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)
    {
        if (piece != null)
        {
            piece.setCurrentPosition(new ChessPosition(position.getRow(), position.getColumn()));
        }
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position)
    {
        ChessPiece piece = board[position.getRow() - 1][position.getColumn() - 1];
        if (piece != null)
        {
            piece.setCurrentPosition(position);
        }
        return piece;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard()
    {
        board = new ChessPiece[8][8];
        setDefaultBoard();
    }

    private void setDefaultBoard()
    {
        // This idea was taken from TestUtilities
        Character[][] repr = new Character[][] {
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };

       Map<Character, ChessPiece.PieceType> charmap = Map.of(
        'p', ChessPiece.PieceType.PAWN,
        'n', ChessPiece.PieceType.KNIGHT,
        'r', ChessPiece.PieceType.ROOK,
        'q', ChessPiece.PieceType.QUEEN,
        'k', ChessPiece.PieceType.KING,
        'b', ChessPiece.PieceType.BISHOP
       );

        for (int rowIdx = 0; rowIdx < repr.length; rowIdx++)
        {
            for (int colIdx = 0; colIdx < repr[rowIdx].length; colIdx++)
            {
                Character current = repr[rowIdx][colIdx];
                ChessPosition pos = new ChessPosition(rowIdx + 1, colIdx + 1);
                ChessPiece piece = null;

                if (current != ' ') {
                    piece = new ChessPiece(
                        Character.isLowerCase(current) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK,
                        charmap.get(Character.toLowerCase(current))
                    );
                }

                addPiece(pos, piece);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
