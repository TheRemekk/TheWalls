package org.example.theremekk.thewalls.managers;

import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.theremekk.thewalls.runnables.ArenaCopyRunnable;

import java.util.*;
import java.util.function.Consumer;

public class WorldManager {
    private static WorldManager instance;
    private final ConfigManager configManager;
    private final WGRegionManager wgRegionManager;
    private Plugin plugin;

    private WorldManager() {
        this.configManager = ConfigManager.getInstance();
        this.wgRegionManager = WGRegionManager.getInstance();
    }

    public static WorldManager getInstance() {
        if (instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }

    public void init(Plugin plugin) {
        if (this.plugin == null) {
            this.plugin = plugin;
            plugin.saveDefaultConfig();
        }
    }

    public World getWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) return world;

        try {
            WorldCreator creator = new WorldCreator(worldName);
            world = creator.createWorld();
            return world;
        } catch (Exception e) {
            configManager.onError("Wystąpił błąd podczas wczytywania świata!");
            e.printStackTrace();
            return null;
        }
    }

    public boolean doesWorldExist(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }

    public Location getLocation(String coordsPath, String worldPath) {
        String worldName = configManager.getString(worldPath + ".world");
        World world = getWorld(worldName);
        return configManager.getLocation(coordsPath, world);
    }

    public Location getLocation(String locPath) {
        return getLocation(locPath, locPath);
    }

    public void createEmptyWorld(String worldName, Consumer<World> callback) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                WorldCreator creator = new WorldCreator(worldName);
                creator.environment(World.Environment.NORMAL);
                creator.type(WorldType.FLAT);
                creator.generatorSettings("{\n" +
                        "  \"structures\": {\n" +
                        "    \"structures\": {}\n" +
                        "  },\n" +
                        "  \"layers\": [],\n" +
                        "  \"biome\": \"plains\"\n" +
                        "}\n");

                World world = creator.createWorld();
                if (world != null) {
                    world.setSpawnLocation(-79,65, -79);
                    world.save();
                    callback.accept(world);
                }
                else {
                    configManager.onError("Nie udało się wczytać utworzonego świata!");
                    callback.accept(null);
                }
            }
            catch (Exception e) {
                configManager.onError("Wystąpił błąd podczas tworzenia świata!");
                e.printStackTrace();
                callback.accept(null);
            }
        });
    }

    public void copyArenaModel(World targetWorld, Consumer<Boolean> callback) {
        String model = configManager.getArenaModelConfSection();
        int x1 = configManager.getInt(model + ".pos_x1");
        int y1 = configManager.getInt(model + ".pos_y1");
        int z1 = configManager.getInt(model + ".pos_z1");
        int x2 = configManager.getInt(model + ".pos_x2");
        int y2 = configManager.getInt(model + ".pos_y2");
        int z2 = configManager.getInt(model + ".pos_z2");

        String worldCopyFromName = configManager.getString(model + ".world");
        World worldCopyFrom = getWorld(worldCopyFromName);
        if (worldCopyFrom == null) {
            callback.accept(false);
            return;
        }

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        List<Block> blocksToCopy = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocksToCopy.add(worldCopyFrom.getBlockAt(x, y, z));
                }
            }
        }

        int totalBlocks = blocksToCopy.size();
        int batchSize = (int) Math.ceil(totalBlocks * 0.05);
        new ArenaCopyRunnable(targetWorld, blocksToCopy, batchSize, callback).runTaskTimer(plugin, 0, 1);
    }

    public void resetWorld(String worldName) {
        World world = getWorld(worldName);
        if(Bukkit.getServer().unloadWorld(world, false)) {
            World newWorld = Bukkit.getServer().createWorld(new WorldCreator(worldName));
            newWorld.setAutoSave(false);
        } else configManager.onError("Nie udalo sie przeladowac mapy: " + worldName);
    }

    public void createWGRegions(World targetWorld) {
        IntegerFlag FEED_MAX_HUNGER = new IntegerFlag("feed-max-hunger");
        String world = targetWorld.getName();
        FileConfiguration config = configManager.getConfig();
        Set<String> keys = config.getConfigurationSection(configManager.getArenaCoordsConfSection()).getKeys(false);

        for (String key : keys) {
            if (config.contains(configManager.getArenaCoordsConfSection() + "." + key + ".x") &&
                    config.contains(configManager.getArenaCoordsConfSection() + "." + key + ".y") &&
                    config.contains(configManager.getArenaCoordsConfSection() + "." + key + ".z") &&
                    config.contains(configManager.getArenaCoordsConfSection() + "." + key + ".x2") &&
                    config.contains(configManager.getArenaCoordsConfSection() + "." + key + ".y2") &&
                    config.contains(configManager.getArenaCoordsConfSection() + "." + key + ".z2")) {

                int x = configManager.getInt(configManager.getArenaCoordsConfSection() + "." + key + ".x");
                int y = configManager.getInt(configManager.getArenaCoordsConfSection() + "." + key + ".y");
                int z = configManager.getInt(configManager.getArenaCoordsConfSection() + "." + key + ".z");
                int x2 = configManager.getInt(configManager.getArenaCoordsConfSection() + "." + key + ".x2");
                int y2 = configManager.getInt(configManager.getArenaCoordsConfSection() + "." + key + ".y2");
                int z2 = configManager.getInt(configManager.getArenaCoordsConfSection() + "." + key + ".z2");

                int minX = Math.min(x, x2);
                int maxX = Math.max(x, x2);
                int minY = Math.min(y, y2);
                int maxY = Math.max(y, y2);
                int minZ = Math.min(z, z2);
                int maxZ = Math.max(z, z2);

                wgRegionManager.createRegion(targetWorld, key, minX, minY, minZ, maxX, maxY, maxZ);
                Map<StateFlag, StateFlag.State> stateFlags = new HashMap<>();
                Map<DoubleFlag, Double> doubleFlags = new HashMap<>();
                Map<IntegerFlag, Integer> intFlags = new HashMap<>();

                if (key.equals("arena")) {
                    stateFlags.put(Flags.MOB_SPAWNING, StateFlag.State.DENY);
                    stateFlags.put(Flags.PVP, StateFlag.State.ALLOW);
                    stateFlags.put(Flags.TNT, StateFlag.State.ALLOW);
                    stateFlags.put(Flags.BLOCK_PLACE, StateFlag.State.ALLOW);
                    stateFlags.put(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
                    wgRegionManager.setCustomRegionFlags(world, key, stateFlags, doubleFlags, intFlags);
                }
                else if (key.equals("deathmatch")) {
                    stateFlags.put(Flags.MOB_SPAWNING, StateFlag.State.DENY);
                    stateFlags.put(Flags.BLOCK_PLACE, StateFlag.State.DENY);
                    stateFlags.put(Flags.BLOCK_BREAK, StateFlag.State.DENY);
                    stateFlags.put(Flags.PVP, StateFlag.State.ALLOW);
                    stateFlags.put(Flags.TNT, StateFlag.State.ALLOW);
                    wgRegionManager.setCustomRegionFlags(world, key, stateFlags, doubleFlags, intFlags);
                }
                else if (key.equals("arena_lobby")) {
                    stateFlags.put(Flags.MOB_SPAWNING, StateFlag.State.DENY);
                    stateFlags.put(Flags.BLOCK_PLACE, StateFlag.State.DENY);
                    stateFlags.put(Flags.BLOCK_BREAK, StateFlag.State.DENY);
                    stateFlags.put(Flags.ITEM_DROP, StateFlag.State.DENY);
                    stateFlags.put(Flags.PVP, StateFlag.State.ALLOW);
                    stateFlags.put(Flags.TNT, StateFlag.State.ALLOW);
                    intFlags.put(FEED_MAX_HUNGER, 20);
                    wgRegionManager.setCustomRegionFlags(world, key, stateFlags, doubleFlags, intFlags);
                }
                else {
                    wgRegionManager.setRegionFlags(world, key, true);
                }
            }
        }
    }
}
