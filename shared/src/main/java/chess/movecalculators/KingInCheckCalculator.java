package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class KingInCheckCalculator
{
    private final ChessBoard board;
    private final List<ChessMove> moves;
    private final TeamColor teamColor;

    public KingInCheckCalculator(ChessBoard board, List<ChessMove> checkMoves, TeamColor kingTeam)
    {
        this.board = board;
        moves = checkMoves;
        teamColor = kingTeam;
    }

    // Returns moves that do not put the piece's team's king in check
    public List<ChessMove> getSafeMoves()
    {
        List<ChessMove> safeMoves = new ArrayList<>();
        TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition friendlyKingPos = board.findPiece(ChessPiece.PieceType.KING).getFirst();

        // Each move that the friendly piece can make
        friendlyMoveLoop:
        for (ChessMove move : moves)
        {
            ChessBoard possibleBoard = board.withMove(move);
            List<ChessPiece> opponentPieces = possibleBoard.getAllOfColor(opponentColor);
            // For each opponent's piece that could threaten the friendly king
            for (ChessPiece opponentPiece : opponentPieces)
            {
                List<ChessMove> opponentMoves = opponentPiece.pieceMoves(possibleBoard);
                // If any move includes the friendly king, this friendly move is unsafe
                for (ChessMove opponentMove: opponentMoves)
                {
                    if (opponentMove.getEndPosition().equals(friendlyKingPos))
                    {
                        // Skip the rest of this and check the next friendly move
                        continue friendlyMoveLoop;
                    }
                }
            }

            // If the loop was not "continue"-d then no opponent pieces could threaten the king
            safeMoves.add(move);
        }

        return safeMoves;
    }
}
