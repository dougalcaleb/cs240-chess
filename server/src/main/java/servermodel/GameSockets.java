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

    public void removePlayer(ChessGame.TeamColor color)
    {
        players.remove(color);
    }

    public void addObserver(Session session)
    {
        observers.add(session);
    }

    public void removeObserver(Session session)
    {
        observers.removeIf((obs) -> obs.equals(session));
    }

    public Session getPlayerSession(ChessGame.TeamColor color)
    {
        return players.get(color);
    }

    public List<Session> getObservers()
    {
        return observers;
    }

    public void removeParticipant(Session session)
    {
        if (observers.contains(session))
        {
            removeObserver(session);
        }
        else if (session.equals(players.get(ChessGame.TeamColor.WHITE)))
        {
            removePlayer(ChessGame.TeamColor.WHITE);
        }
        else if (session.equals(players.get(ChessGame.TeamColor.BLACK)))
        {
            removePlayer(ChessGame.TeamColor.BLACK);
        }
    }

    public void removeAll()
    {
        observers.clear();
        players.clear();
    }

    public boolean isEmpty()
    {
        return observers.isEmpty() && players.isEmpty();
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
