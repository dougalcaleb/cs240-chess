package websocket.messages;

import chess.ChessPosition;

import java.util.List;

public class LegalMovesMessage extends ServerMessage {
    public List<ChessPosition> positions;

    public LegalMovesMessage(String message, List<ChessPosition> positions) {
        super(ServerMessageType.LEGAL_MOVES, message);
        this.positions = positions;
    }
}
