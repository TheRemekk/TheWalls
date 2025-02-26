package org.example.theremekk.thewalls.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;

public class OnWorldChange implements Listener {

    private final Plugin plugin;
    public OnWorldChange(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        World worldfrom = event.getFrom();
        for (Player p : world.getPlayers()) {
            p.showPlayer(plugin, player);
            player.showPlayer(plugin, p);
        }
        for (Player p : worldfrom.getPlayers()) {
            p.hidePlayer(plugin, player);
            player.hidePlayer(plugin, p);
        }
    }
}
