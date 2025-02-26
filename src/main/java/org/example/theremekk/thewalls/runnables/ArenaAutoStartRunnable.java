package org.example.theremekk.thewalls.runnables;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.ConfigManager;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;

import java.util.HashMap;
import java.util.Map;

public class ArenaAutoStartRunnable extends BukkitRunnable {
    private final ArenaManager arenaManager;
    private final ConfigManager configManager;
    private final Plugin plugin;

    private final Map<String, ArenaRunnable> activeRunnables = new HashMap<>();

    public ArenaAutoStartRunnable(Plugin plugin) {
        this.plugin = plugin;
        this.arenaManager = ArenaManager.getInstance();
        this.configManager = ConfigManager.getInstance();
    }

    @Override
    public void run() {
        for (Arena arena : arenaManager.getArenas().values()) {
            if (arena.getStage() != ArenaStage.ENABLED) {
                continue;
            }

            int activePlayers = arena.getActivePlayers().size();
            int minPlayersArena = configManager.getInt(configManager.getArenaConfSection(arena.getName()) + ".min_players_arena");

            if (activePlayers == 0 && activeRunnables.containsKey(arena.getName())) {
                activeRunnables.get(arena.getName()).cancel();
                activeRunnables.remove(arena.getName());
                arena.setIngameStage(null); 
            }
            else if (activePlayers > 0 && arena.getIngameStage() == null) {
                ArenaRunnable arenaRunnable = new ArenaRunnable(arena);
                arenaRunnable.runTaskTimer(plugin, 0, 20);
                activeRunnables.put(arena.getName(), arenaRunnable);
                arena.setIngameStage(ArenaIngameStage.WAITING);
            }

            if (activePlayers >= minPlayersArena && arena.getIngameStage() == ArenaIngameStage.WAITING) {
                arena.setIngameStage(ArenaIngameStage.STARTING);
            }

            if (activePlayers < minPlayersArena && arena.getIngameStage() == ArenaIngameStage.STARTING && !arena.isForceStarted()) {
                if (activeRunnables.containsKey(arena.getName())) {
                    activeRunnables.get(arena.getName()).cancel();
                    activeRunnables.remove(arena.getName());
                }
                arena.setIngameStage(ArenaIngameStage.WAITING);
                ArenaRunnable arenaRunnable = new ArenaRunnable(arena);
                arenaRunnable.runTaskTimer(plugin, 0, 20);
                activeRunnables.put(arena.getName(), arenaRunnable);
            }
        }
    }
}