package chess;

import chess.ChessGame.TeamColor;

import java.util.*;

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

    public ChessBoard(ChessBoard copySource)
    {
        this.board = copySource.board.clone();
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

    public List<ChessPosition> findPiece(ChessPiece.PieceType piece)
    {
        List<ChessPosition> pieces = new ArrayList<>();
        for (int row = 1; row <= 8; row++)
        {
            for (int col = 1; col <= 8; col++)
            {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece pieceAtPos = getPiece(pos);

                if (pieceAtPos != null && pieceAtPos.getPieceType() == piece)
                {
                    pieces.add(pos);
                }
            }
        }

        return pieces;
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
                        Character.isLowerCase(current) ? TeamColor.WHITE : TeamColor.BLACK,
                        charmap.get(Character.toLowerCase(current))
                    );
                }

                addPiece(pos, piece);
            }
        }
    }

    public ChessBoard withMove(ChessMove move)
    {
        ChessBoard modifiedBoard = new ChessBoard(this);
        ChessPiece pieceMoved = modifiedBoard.getPiece(move.getStartPosition());
        modifiedBoard.addPiece(move.getStartPosition(), null);
        modifiedBoard.addPiece(move.getEndPosition(), pieceMoved);
        return modifiedBoard;
    }

    public List<ChessPiece> getAllOfColor(TeamColor color)
    {
        List<ChessPiece> pieces = new ArrayList<>();
        for (int row = 1; row <= 8; row++)
        {
            for (int col = 1; col <= 8; col++)
            {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece pieceAtPos = getPiece(pos);

                if (pieceAtPos != null && pieceAtPos.getTeamColor() == color)
                {
                    pieces.add(pieceAtPos);
                }
            }
        }

        return pieces;
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
