package org.example.theremekk.thewalls.storage;

import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.enums.ArenaPlayerRoles;
import org.example.theremekk.thewalls.enums.ArenaStage;

import java.util.*;
import java.util.stream.Collectors;

public class Arena {
    private final String name;
    private final Map<UUID, ArenaPlayerRoles> players;
    private final Teams team;
    private ArenaStage stage;
    private ArenaIngameStage ingameStage;
    private boolean isForceStarted;

    private static final ArenaPlayerRoles DEFAULT_ROLE = ArenaPlayerRoles.GORNIK;

    public Arena(String name) {
        this.name = name;
        this.players = new HashMap<>();
        this.team = new Teams();
        this.stage = ArenaStage.ENABLED;
        this.ingameStage = null;
        isForceStarted = false;
    }

    public Teams getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }

    public ArenaStage getStage() {
        return stage;
    }

    public ArenaIngameStage getIngameStage() {
        return ingameStage;
    }

    public void setStage(ArenaStage stage) {
        this.stage = stage;
    }

    public void setIngameStage(ArenaIngameStage ingameStage) {
        this.ingameStage = ingameStage;
    }

    public void addPlayer(UUID playerUUID) {
        players.put(playerUUID, DEFAULT_ROLE);
    }

    public void removePlayer(UUID playerUUID) {
        players.remove(playerUUID);
        if (team.getPlayerTeam(playerUUID) != null) {
            int team_index = team.getPlayerTeam(playerUUID);
            team.removePlayer(team_index, playerUUID);
        }
    }

    public Set<UUID> getAllPlayers() {
        return players.keySet();
    }

    public Set<UUID> getActivePlayers() {
        return players.entrySet().stream()
                .filter(entry -> entry.getValue() != ArenaPlayerRoles.SPECTATOR)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<UUID> getSpectators() {
        return players.entrySet().stream()
                .filter(entry -> entry.getValue() == ArenaPlayerRoles.SPECTATOR)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }


    public boolean hasPlayer(UUID playerUUID) {
        return players.containsKey(playerUUID);
    }

    public void setPlayerRole(UUID playerUUID, ArenaPlayerRoles role) {
        players.put(playerUUID, role);
    }

    public ArenaPlayerRoles getPlayerRole(UUID playerUUID) {
        return players.get(playerUUID);
    }

    public boolean isForceStarted() {
        return isForceStarted;
    }

    public void setForceStarted(boolean forceStarted) {
        this.isForceStarted = forceStarted;
    }

    public boolean isActiveParticipant(UUID playerUUID) {
        ArenaPlayerRoles role = getPlayerRole(playerUUID);
        return role != null && role != ArenaPlayerRoles.SPECTATOR;
    }
}


