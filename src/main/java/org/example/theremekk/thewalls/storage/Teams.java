package org.example.theremekk.thewalls.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Teams {

    private final Map<Integer, Set<UUID>> teams;

    public Teams() {
        this.teams = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            teams.put(i, new HashSet<>());
        }
    }

    public Set<UUID> getTeam(int index) {
        return teams.get(index);
    }

    public void addPlayer(int index, UUID playerUUID) {
        Set<UUID> team = getTeam(index);
        team.add(playerUUID);
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.setPlayerListName(ColorUtils.colorize(ColorUtils.getTeamColor(index) + player.getName()));
        }
    }

    public void removePlayer(int index, UUID playerUUID) {
        Set<UUID> team = getTeam(index);
        team.remove(playerUUID);
    }

    public void clear(int index) {
        Set<UUID> team = getTeam(index);
        team.clear();
    }

    public Integer getPlayerTeam(UUID playerUUID) {
        for (Map.Entry<Integer, Set<UUID>> entry : teams.entrySet()) {
            if (entry.getValue().contains(playerUUID)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean teamExists(int teamIndex) {
        return teams.containsKey(teamIndex);
    }
}
