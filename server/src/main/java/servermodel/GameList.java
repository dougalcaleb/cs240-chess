package servermodel;

import sharedmodel.GameData;

import java.util.Collection;

public record GameList(Collection<GameData> games) {
}
