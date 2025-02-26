package org.example.theremekk.thewalls.listeners;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.ConfigManager;
import org.example.theremekk.thewalls.managers.WorldManager;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;
import org.example.theremekk.thewalls.storage.Teams;

import java.util.ArrayList;
import java.util.List;

public class OnPlayerDeath implements Listener {
    private final ArenaManager arenaManager;
    private final ConfigManager configManager;
    private final WorldManager worldManager;

    public OnPlayerDeath() {
        this.arenaManager = ArenaManager.getInstance();
        this.configManager = ConfigManager.getInstance();
        this.worldManager = WorldManager.getInstance();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        String swordsUnicode = "⚔", skullUnicode = "☠";

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) e.getEntity();
        Arena arena = arenaManager.getArenaOfPlayer(victim.getUniqueId());

        if (arena == null) {
            return;
        }

        if (arena.getSpectators().contains(victim.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if (arena.getStage() != ArenaStage.INGAME) {
            e.setCancelled(true);
            return;
        }

        Teams teams = arena.getTeam();
        String worldPath = configManager.getArenaConfSection(arena.getName()) + ".world";
        World world = worldManager.getWorld(configManager.getString(worldPath));

        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) e;
            Entity damager = damageByEntityEvent.getDamager();
            Player attacker = null;

            if (damager instanceof Player) {
                attacker = (Player) damager;
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    attacker = (Player) projectile.getShooter();
                }
            }
            else {
                return;
            }

            if (attacker == null) {
                return;
            }

            if (arena.getSpectators().contains(attacker.getUniqueId())) {
                e.setCancelled(true);
                return;
            }

            if (teams.getPlayerTeam(victim.getUniqueId()).equals(teams.getPlayerTeam(attacker.getUniqueId()))) {
                e.setCancelled(true);
                return;
            }

            if ((victim.getHealth() - e.getDamage()) > 0) {
                return;
            }

            victim.sendMessage(ColorUtils.colorize(
                    "&7Zostales zabity przez "
                            + ColorUtils.getTeamPrefix(teams.getPlayerTeam(attacker.getUniqueId()))
                            + attacker.getDisplayName()
            ));

            List<Player> players = new ArrayList<>(world.getPlayers());
            players.remove(victim);

            for (Player player : players) {
                player.sendMessage(ColorUtils.colorize(
                        ColorUtils.getTeamPrefix(teams.getPlayerTeam(attacker.getUniqueId()))
                                + attacker.getDisplayName() + " &7" + swordsUnicode + " "
                                + ColorUtils.getTeamPrefix(teams.getPlayerTeam(victim.getUniqueId()))
                                + victim.getDisplayName()
                ));
            }
        } else {
            if ((victim.getHealth() - e.getDamage()) > 0) {
                return;
            }

            for (Player player : world.getPlayers()) {
                player.sendMessage(ColorUtils.colorize(
                        "&7" + skullUnicode + " "
                                + ColorUtils.getTeamPrefix(teams.getPlayerTeam(victim.getUniqueId()))
                                + victim.getDisplayName()
                ));
            }
        }
        e.setCancelled(true);
        arenaManager.setPlayerAsSpectator(victim.getUniqueId());
    }
}
