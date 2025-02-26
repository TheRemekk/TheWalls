package org.example.theremekk.thewalls.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.runnables.TNTRunnable;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;

public class OnBlockPlace implements Listener {
    private final Plugin plugin;
    private final ArenaManager arenaManager;

    public OnBlockPlace(Plugin plugin) {
        this.plugin = plugin;
        this.arenaManager = ArenaManager.getInstance();
    }

    @EventHandler
    void onBlockPlaceEvent(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Arena arena = arenaManager.getArenaOfPlayer(p.getUniqueId());

        if (arena == null) {
            return;
        }

        Block block = e.getBlock();

        if(block.getType() != Material.TNT) {
            return;
        }

        if (arena.getIngameStage() == ArenaIngameStage.COMBAT) {
            e.setCancelled(true);
            block.setType(Material.AIR);

            World world = block.getWorld();
            Location loc = block.getLocation();
            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc, EntityType.PRIMED_TNT);
            tnt.setFuseTicks(40); // 2 sekundy (40 ticków)

            new TNTRunnable(loc).runTaskLater(plugin,40L);
        }
        else {
            e.setCancelled(true);
            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie mozna uzywac TNT na tym etapie rozgrywki"));
        }
    }
}
