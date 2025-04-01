package servermodel;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameSockets {
    private final List<Session> observers = new ArrayList<>();
    private final HashMap<ChessGame.TeamColor, Session> players = new HashMap<>();

    public GameSockets()
    {

    }

    public void addPlayer(Session session, ChessGame.TeamColor color)
    {
        players.put(color, session);
    }

    public void addObserver(Session session)
    {
        observers.add(session);
    }

    public Session getPlayerSession(ChessGame.TeamColor color)
    {
        return players.get(color);
    }

    public List<Session> getObservers()
    {
        return observers;
    }

    public List<Session> getParticipants()
    {
        List<Session> participants = new ArrayList<>();
        participants.addAll(observers);
        participants.addAll(players.values());
        return  participants;
    }

    public List<Session> getPlayers()
    {
        return (List<Session>) players.values();
    }
}
