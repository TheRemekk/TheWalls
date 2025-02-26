package org.example.theremekk.thewalls.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.ItemManager;
import org.example.theremekk.thewalls.managers.ScoreboardManager;

import java.util.UUID;

public class OnPlayerJoin implements Listener {
    private final Plugin plugin;
    private final ArenaManager arenaManager;
    private final ItemManager itemManager;
    private final ScoreboardManager scoreboardManager;

    public OnPlayerJoin(Plugin plugin) {
        this.plugin = plugin;
        this.arenaManager = ArenaManager.getInstance();
        this.itemManager = ItemManager.getInstance();
        this.scoreboardManager = ScoreboardManager.getInstance();
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player p = (Player) e.getPlayer();
        UUID playerUUID = p.getUniqueId();
        World world = p.getWorld();

        p.getInventory().clear();
        ItemStack thewalls_gui = itemManager.getItem("thewalls_gui");
        p.getInventory().setItem(0, thewalls_gui);

        arenaManager.removePlayerCompletely(playerUUID);
        scoreboardManager.createScoreboard(p);
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getWorld() == world) {
                player.showPlayer(plugin, p);
                p.showPlayer(plugin, player);
            }
            else {
                player.hidePlayer(plugin, p);
                p.hidePlayer(plugin, player);
            }
        }
    }
}
