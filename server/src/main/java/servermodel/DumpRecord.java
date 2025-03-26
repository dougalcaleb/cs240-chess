package servermodel;

import sharedmodel.GameData;
import sharedmodel.UserData;

import java.util.ArrayList;
import java.util.Collection;

public record DumpRecord(Collection<UserData> userData, Collection<ArrayList<String>> authData, Collection<GameData> gameData) {
}
