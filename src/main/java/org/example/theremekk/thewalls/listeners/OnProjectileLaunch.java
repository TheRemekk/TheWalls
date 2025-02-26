package org.example.theremekk.thewalls.listeners;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.EnumSet;

public class OnProjectileLaunch implements Listener {
    private final ArenaManager arenaManager;

    public OnProjectileLaunch() {
        this.arenaManager = ArenaManager.getInstance();
    }

    @EventHandler
    void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile projectile = e.getEntity();
        if(!(projectile instanceof EnderPearl)) {
            return;
        }

        EnderPearl pearl = (EnderPearl) projectile;
        ProjectileSource source = pearl.getShooter();

        if (!(source instanceof Player)) {
            return;
        }

        Player shooter = (Player) source;
        Arena arena = arenaManager.getArenaOfPlayer(shooter.getUniqueId());

        if (arena == null) {
            return;
        }

        EnumSet<ArenaIngameStage> validStages = EnumSet.of(
                ArenaIngameStage.COMBAT,
                ArenaIngameStage.DEATHMATCH
        );

        if(!validStages.contains(arena.getIngameStage())) {
            e.setCancelled(true);
            shooter.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie mozna uzywac perel na tym etapie rozgrywki"));
        }
    }
}
