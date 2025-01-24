package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KingInCheckCalculator
{
    // Returns moves that do not put the piece's team's king in check
    public static List<ChessMove> getSafeMoves(ChessBoard board, List<ChessMove> moves, TeamColor teamColor)
    {
        List<ChessMove> safeMoves = new ArrayList<>();
        TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;

        // Each move that the friendly piece can make
        friendlyMoveLoop:
        for (ChessMove move : moves)
        {
            ChessBoard possibleBoard = board.withMove(move);
            ChessPosition friendlyKingPos = possibleBoard.findPiece(ChessPiece.PieceType.KING, teamColor).getFirst();
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

    public static boolean isInCheck(ChessBoard board, TeamColor color)
    {
        TeamColor opponentColor = color == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition friendlyKingPos = board.findPiece(ChessPiece.PieceType.KING, color).getFirst();
        List<ChessPiece> opponentPieces = board.getAllOfColor(opponentColor);
        // For each opponent's piece that could threaten the friendly king
        for (ChessPiece opponentPiece : opponentPieces)
        {
            List<ChessMove> opponentMoves = opponentPiece.pieceMoves(board);
            // If any move includes the friendly king, this friendly move is unsafe
            for (ChessMove opponentMove: opponentMoves)
            {
                if (opponentMove.getEndPosition().equals(friendlyKingPos))
                {
                    // Skip the rest of this and check the next friendly move
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean safeMovesExist(ChessBoard board, TeamColor color)
    {
        List<ChessPiece> ownPieces = board.getAllOfColor(color);
        List<ChessMove> allPossibleMoves = new ArrayList<>();

        for (ChessPiece teammate : ownPieces)
        {
            allPossibleMoves.addAll(teammate.pieceMoves());
        }

        return !KingInCheckCalculator.getSafeMoves(board, allPossibleMoves, color).isEmpty();
    }

    public static boolean isSafeMove(ChessBoard board, TeamColor color, ChessMove move)
    {
        List<ChessMove> safe = KingInCheckCalculator.getSafeMoves(board, new ArrayList<ChessMove>(Collections.singletonList(move)), color);

        for (ChessMove safeMove : safe)
        {
            if (safeMove.equals(move))
            {
                return true;
            }
        }

        return false;
    }
}
