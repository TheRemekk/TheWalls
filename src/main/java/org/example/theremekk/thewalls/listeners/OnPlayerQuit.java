package org.example.theremekk.thewalls.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.ScoreboardManager;

import java.util.UUID;

public class OnPlayerQuit implements Listener {
    private final ArenaManager arenaManager;
    private final ScoreboardManager scoreboardManager;

    public OnPlayerQuit() {
        this.arenaManager = ArenaManager.getInstance();
        this.scoreboardManager = ScoreboardManager.getInstance();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UUID playerUUID = p.getUniqueId();
        arenaManager.removePlayerCompletely(playerUUID);
        scoreboardManager.removeScoreboard(p);
    }
}
