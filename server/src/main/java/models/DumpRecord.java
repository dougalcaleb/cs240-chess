package models;

import model.UserData;

import java.util.Collection;

public record DumpRecord(Collection<UserData> userData, Collection<String> authData) {
}
