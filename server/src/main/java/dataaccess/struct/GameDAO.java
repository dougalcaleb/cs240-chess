package dataaccess.struct;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public Collection<GameData> getAllAsList();
}
