package sharedmodel;

import chess.ChessPiece;

public class MoveMadeResult {

    public ChessPiece piece;
    public boolean resultedInCheck = false;
    public boolean resultedInCheckmate = false;

    public MoveMadeResult(ChessPiece piece, boolean check, boolean checkmate)
    {
        this.piece = piece;
        resultedInCheck = check;
        resultedInCheckmate = checkmate;
    }
}
