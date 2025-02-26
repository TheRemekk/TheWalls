package org.example.theremekk.thewalls.managers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.enums.ArenaPlayerRoles;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;
import org.example.theremekk.thewalls.storage.Teams;

import java.util.*;

public class ArenaManager {

    private static ArenaManager instance;
    private final ConfigManager configManager;
    private final ItemManager itemManager;
    private final WorldManager worldManager;
    private final ScoreboardManager scoreboardManager;
    private final Map<String, Arena> arenas;
    private final Map<UUID, String> pendingArenas;
    private final Map<ArenaIngameStage, Integer> ingameStageTimes = new HashMap<>();

    private ArenaManager() {
        this.configManager = ConfigManager.getInstance();
        this.worldManager = WorldManager.getInstance();
        this.itemManager = ItemManager.getInstance();
        this.scoreboardManager = ScoreboardManager.getInstance();
        arenas = new HashMap<>();
        pendingArenas = new HashMap<>();
        loadArenasFromConfig();
        loadStageTimes();
    }

    public static ArenaManager getInstance() {
        if (instance == null) {
            instance = new ArenaManager();
        }
        return instance;
    }

    public Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    public void createArena(String arenaName) {
        arenas.put(arenaName, new Arena(arenaName));
    }

    public void removeArena(String arenaName) {
        arenas.remove(arenaName);
    }

    public Arena getArenaOfPlayer(UUID playerUUID) {
        for (Arena arena : arenas.values()) {
            if (arena.hasPlayer(playerUUID)) {
                return arena;
            }
        }
        return null;
    }

    public void setPlayerAsSpectator(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        Arena arena = getArenaOfPlayer(playerUUID);
        if (arena == null) return;

        Teams team = arena.getTeam();
        Integer teamIndex = team.getPlayerTeam(playerUUID);
        if (teamIndex != null) {
            team.removePlayer(teamIndex, playerUUID);
        }

        arena.setPlayerRole(playerUUID, ArenaPlayerRoles.SPECTATOR);
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0);
        player.setFoodLevel(20);
        player.setHealth(20);

        ItemStack arenaPlayersGui = itemManager.getItem("arena_players_gui");
        ItemStack arenaLobby = itemManager.getItem("arena_lobby");
        player.getInventory().setItem(0, arenaPlayersGui);
        player.getInventory().setItem(8, arenaLobby);
    }

    public void removePlayerCompletely(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        Arena arena = getArenaOfPlayer(playerUUID);
        if (arena != null) {
            arena.removePlayer(playerUUID);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.setAllowFlight(false);
        player.setFlying(false);

        player.setPlayerListName(ColorUtils.colorize("&f" + player.getName()));
        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0);
        player.setFoodLevel(20);
        player.setHealth(20);

        scoreboardManager.updateScoreboardTitle(player, "", "");
        String locPath = configManager.getLobbyLocConfSection();
        Location lobby = worldManager.getLocation(locPath);
        player.teleport(lobby);

        ItemStack thewalls_gui = itemManager.getItem("thewalls_gui");
        player.getInventory().setItem(0, thewalls_gui);
    }

    public void addPlayerToArenaCreation(UUID playerUUID, String arenaName) {
        pendingArenas.put(playerUUID, arenaName);
    }

    public void removePlayerFromArenaCreation(UUID playerUUID) {
        pendingArenas.remove(playerUUID);
    }

    public boolean isPlayerCreatingArena(UUID playerUUID) {
        return pendingArenas.containsKey(playerUUID);
    }

    public String getNameOfCreatedArena(UUID playerUUID) {
        return pendingArenas.get(playerUUID);
    }

    public void loadArenasFromConfig() {
        String arenasSectionName = configManager.getArenasConfSection();
        ConfigurationSection arenasSection = configManager.getConfig().getConfigurationSection(arenasSectionName);

        if (arenasSection == null) {
            Bukkit.getLogger().warning("Nie znaleziono sekcji: " + arenasSectionName + " w pliku konfiguracyjnym!");
            return;
        }

        for (String arenaName : arenasSection.getKeys(false)) {
            createArena(arenaName);
        }
    }

    private void loadStageTimes() {
        for (ArenaIngameStage stage : ArenaIngameStage.values()) {
            double timeInMinutes = configManager.getDouble(configManager.getArenaCountersConfSection() + "." + stage.name().toLowerCase()) * 60;
            int time = (int) timeInMinutes;
            ingameStageTimes.put(stage, time);
        }
    }

    public int getStageTime(ArenaIngameStage stage) {
        return ingameStageTimes.getOrDefault(stage, 0);
    }
}

