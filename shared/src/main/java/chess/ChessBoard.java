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
        board = new ChessPiece[8][8];
        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            for (int colIdx = 0; colIdx < 8; colIdx++) {
                if ( copySource.board[rowIdx][colIdx] != null )
                {
                    board[rowIdx][colIdx] = new ChessPiece(copySource.board[rowIdx][colIdx]);
                } else {
                    board[rowIdx][colIdx] = null;
                }
            }
        }
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

    public void applyMove(ChessMove move)
    {
        ChessPiece pieceMoved = getPiece(move.getStartPosition());
        addPiece(move.getStartPosition(), null);
        addPiece(move.getEndPosition(), pieceMoved);
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

    public List<ChessPosition> findPiece(ChessPiece.PieceType piece, TeamColor color)
    {
        List<ChessPosition> pieces = new ArrayList<>();
        for (int row = 1; row <= 8; row++)
        {
            for (int col = 1; col <= 8; col++)
            {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece pieceAtPos = getPiece(pos);

                if (pieceAtPos != null && pieceAtPos.getPieceType() == piece && pieceAtPos.getTeamColor() == color)
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
        modifiedBoard.applyMove(move);
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
    public String toString()
    {
        Map<ChessPiece.PieceType, Character> charmap = Map.of(
            ChessPiece.PieceType.PAWN, 'p',
            ChessPiece.PieceType.KNIGHT, 'n',
            ChessPiece.PieceType.ROOK, 'r',
            ChessPiece.PieceType.QUEEN, 'q',
            ChessPiece.PieceType.KING, 'k',
            ChessPiece.PieceType.BISHOP, 'b'
        );

        StringBuilder output = new StringBuilder("+-+-+-+-+-+-+-+-+");

        for (int row = 1; row <= 8; row++)
        {
            output.append("\n|");
            for (int col = 1; col <= 8; col++)
            {
                ChessPiece piece = getPiece(new ChessPosition(row, col));
                Character pieceRep = ' ';

                if (piece != null) {
                    pieceRep = charmap.get(piece.getPieceType());
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        pieceRep = Character.toUpperCase(pieceRep);
                    }
                }

                output.append(pieceRep);
                output.append("|");
            }
        }

        output.append("\n+-+-+-+-+-+-+-+-+");
        return output.toString();
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
