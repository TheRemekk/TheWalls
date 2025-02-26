package org.example.theremekk.thewalls.runnables;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.enums.ArenaPlayerRoles;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.*;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;
import org.example.theremekk.thewalls.storage.Teams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArenaRunnable extends BukkitRunnable {
    private final Arena arena;
    private final ArenaManager arenaManager;
    private final WorldManager worldManager;
    private final ConfigManager configManager;
    private final ScoreboardManager scoreboardManager;
    private final ItemManager itemManager;
    private final WGRegionManager wgRegionManager;
    private final int maxPlayersArena;
    List<Block> wallBlocksList = new ArrayList<>();
    private int timeLeft = 0;

    public ArenaRunnable(Arena arena) {
        this.arena = arena;
        this.arenaManager = ArenaManager.getInstance();
        this.configManager = ConfigManager.getInstance();
        this.worldManager = WorldManager.getInstance();
        this.scoreboardManager = ScoreboardManager.getInstance();
        this.itemManager = ItemManager.getInstance();
        this.wgRegionManager = WGRegionManager.getInstance();
        timeLeft = arenaManager.getStageTime(ArenaIngameStage.STARTING);
        this.maxPlayersArena = configManager.getInt(configManager.getArenaConfSection(arena.getName()) + ".max_players_arena");
    }

    @Override
    public void run() {
        if (arena.getIngameStage() == ArenaIngameStage.WAITING) {
            for (UUID playerUUID : arena.getAllPlayers()) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    String players = arena.getAllPlayers().size() + "/" + maxPlayersArena;
                    scoreboardManager.updateScoreboardTitle(player, "00:00", players);
                }
            }
            return;
        }

        if (timeLeft > 0) {
            timeLeft--;
            for (UUID playerUUID: arena.getAllPlayers()) {
                Player player = Bukkit.getPlayer(playerUUID);
                String players = arena.getAllPlayers().size() + "/" + maxPlayersArena;
                scoreboardManager.updateScoreboardTitle(player, formatTime(timeLeft), players);
            }
        } else {
            switch (arena.getIngameStage()) {
                case STARTING:
                    onEndingStartingStage();
                    break;
                case PREPARATION:
                    onEndingPreparationStage();
                    break;
                case COMBAT:
                    onEndingCombatStage();
                    break;
                case DEATHMATCH:
                    onEndingDeathmatchStage();
                    break;
                default:
                    cancel();
                    return;
            }

            if (!nextStage()) {
                cancel();
            }
        }

        if (arena.getIngameStage() != ArenaIngameStage.STARTING && teamWonGame() != 0) {
            int winner_team_idx = teamWonGame();
            endArenaGame("&7Koniec rozgrywki! Wygrywa drużyna " + ColorUtils.getTeamColor(winner_team_idx) + winner_team_idx + "&7!");
        }
    }

    private void onEndingStartingStage() {
        Teams arenaTeam = arena.getTeam();
        String arenaCoordsPath = configManager.getArenaCoordsConfSection();
        World arenaWorld = getArenaWorld();
        arenaWorld.setTime(0);
        arenaWorld.setStorm(false);
        arenaWorld.setThundering(false);
        arena.setStage(ArenaStage.INGAME);
        for (UUID playerUUID : arena.getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);

            if (player != null) {
                player.sendMessage(ColorUtils.colorizeWithPrefix(
                        "&7Arena się rozpoczęła, powodzenia!"));

                if(!arena.isActiveParticipant(playerUUID)) {
                    continue;
                }

                player.getInventory().clear();
                for (ArenaPlayerRoles role : ArenaPlayerRoles.values()) {
                    if (role == ArenaPlayerRoles.SPECTATOR) {
                        continue;
                    }

                    if (arena.getPlayerRole(playerUUID).equals(role)) {
                        String itemSetName = "kit_" + role.name().toLowerCase();
                        List<ItemStack> items = itemManager.getItemSet(itemSetName);
                        for (ItemStack item : items) {
                            player.getInventory().addItem(item);
                        }
                        player.sendMessage(ColorUtils.colorizeWithPrefix(
                                "&7Otrzymales zestaw " + role.name().toLowerCase() + "a !"));
                    }
                }
                if(arenaTeam.getPlayerTeam(playerUUID) == null) {
                    if(arenaTeam.getTeam(1).size() <= arenaTeam.getTeam(2).size() &&
                            arenaTeam.getTeam(1).size() <= arenaTeam.getTeam(3).size() &&
                            arenaTeam.getTeam(1).size() <= arenaTeam.getTeam(4).size()) {
                        arenaTeam.addPlayer(1, playerUUID);
                        player.setPlayerListName(ColorUtils.colorize("&a" + player.getName()));
                    }
                    else if(arenaTeam.getTeam(2).size() <= arenaTeam.getTeam(1).size()) {
                        arenaTeam.addPlayer(2, playerUUID);
                        player.setPlayerListName(ColorUtils.colorize("&b" + player.getName()));
                    }
                    else if(arenaTeam.getTeam(3).size() <= arenaTeam.getTeam(2).size()) {
                        arenaTeam.addPlayer(3, playerUUID);
                        player.setPlayerListName(ColorUtils.colorize("&c" + player.getName()));
                    }
                    else {
                        arenaTeam.addPlayer(4, playerUUID);
                        player.setPlayerListName(ColorUtils.colorize("&e" + player.getName()));
                    }
                }

                int team_index = arenaTeam.getPlayerTeam(playerUUID);
                Location loc =
                        configManager.getLocation(arenaCoordsPath + ".team" + team_index,
                                arenaWorld);
                player.teleport(loc);
            }
        }
        loadWalls(arenaWorld);
    }

    private void onEndingPreparationStage() {
        World arenaWorld = getArenaWorld();
        removeWalls(arenaWorld);

        for (UUID playerUUID : arena.getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(ColorUtils.colorizeWithPrefix(
                        "&7Mury zostaly zniszczone, powodzenia!"));
            }
        }
    }

    private void onEndingCombatStage() {
        Teams arenaTeam = arena.getTeam();
        String arenaCoordsPath = configManager.getArenaCoordsConfSection();
        World arenaWorld = getArenaWorld();
        for (UUID playerUUID : arena.getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(ColorUtils.colorizeWithPrefix(
                        "&7Deathmatch sie rozpoczal!"));

                if(!arena.isActiveParticipant(playerUUID)) {
                    continue;
                }

                int team_index = arenaTeam.getPlayerTeam(playerUUID);
                Location loc =
                        configManager.getLocation(arenaCoordsPath + ".team" + team_index + "_dm",
                                arenaWorld);
                player.teleport(loc);
            }
        }
    }

    private void onEndingDeathmatchStage() {
        endArenaGame("&7Koniec rozgrywki! Remis, nie ma wygranej druzyny!");
    }

    private void endArenaGame(String messageToPlayers) {
        List<UUID> playersToRemove = new ArrayList<>();
        for (UUID playerUUID : arena.getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null) {
                player.sendMessage(ColorUtils.colorizeWithPrefix(messageToPlayers));
            }
            playersToRemove.add(playerUUID);
        }

        for (UUID playerUUID : playersToRemove) {
            arenaManager.removePlayerCompletely(playerUUID);
        }

        arena.setStage(ArenaStage.RESTARTING);
        worldManager.resetWorld(getArenaWorldName());
        arena.setStage(ArenaStage.ENABLED);
        arena.setIngameStage(null);
        arena.setForceStarted(false);
        cancel();
    }

    private boolean nextStage() {
        List<ArenaIngameStage> stages = Arrays.stream(ArenaIngameStage.values())
                .filter(stage -> stage != ArenaIngameStage.WAITING)
                .collect(Collectors.toList());

        int currentIndex = stages.indexOf(arena.getIngameStage());

        if (currentIndex + 1 < stages.size()) {
            arena.setIngameStage(stages.get(currentIndex + 1));
            timeLeft = arenaManager.getStageTime(arena.getIngameStage());
            return true;
        }
        return false;
    }

    private List<String> getWallPaths() {
        List<String> wallPathList = new ArrayList<>();
        String wallPath = "wall_team";
        for (int i = 1; i <= 4; i++) {
            wallPath += i;
            for (int j = 1; j <= 2; j++) {
                wallPath += j;
                wallPathList.add(wallPath);
                wallPath = wallPath.substring(0, wallPath.length() - 1);
            }
            wallPath = wallPath.substring(0, wallPath.length() - 1);
        }
        return wallPathList;
    }

    private void loadWalls(World world) {
        for (String wallPath: getWallPaths()) {
            String arenaCoordsConfSection = configManager.getArenaCoordsConfSection();
            int x1 = configManager.getInt(arenaCoordsConfSection + "." + wallPath + ".x");
            int y1 = configManager.getInt(arenaCoordsConfSection + "." + wallPath + ".y");
            int z1 = configManager.getInt(arenaCoordsConfSection + "." + wallPath + ".z");
            int x2 = configManager.getInt(arenaCoordsConfSection + "." + wallPath + ".x2");
            int y2 = configManager.getInt(arenaCoordsConfSection + "." + wallPath + ".y2");
            int z2 = configManager.getInt(arenaCoordsConfSection + "." + wallPath + ".z2");

            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            int minZ = Math.min(z1, z2);
            int maxZ = Math.max(z1, z2);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (block.getType() != Material.AIR) {
                            wallBlocksList.add(block);
                        }
                    }
                }
            }
        }
    }

    private void removeWalls(World world) {
        for (Block block: wallBlocksList) {
            block.setType(Material.AIR);
        }

        for (String wallPath: getWallPaths()) {
            wgRegionManager.setRegionFlags(world.getName(), wallPath, false);
        }
    }

    private World getArenaWorld() {
        String arenaWorldName = getArenaWorldName();
        return worldManager.getWorld(arenaWorldName);
    }

    private String getArenaWorldName() {
        String arenaWorldNamePath = configManager.getArenaConfSection(arena.getName()) + ".world";
        return configManager.getString(arenaWorldNamePath);
    }

    private String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private int teamWonGame() {
        Teams teams = arena.getTeam();
        int team_index = 0;
        int counter = 0;
        for (int i = 1; i <= 4; i++) {
            if(teams.getTeam(i).isEmpty()) counter++;
            else team_index = i;
        }
        if (counter >= 3) {
            return team_index;
        } else return 0;
    }
}
