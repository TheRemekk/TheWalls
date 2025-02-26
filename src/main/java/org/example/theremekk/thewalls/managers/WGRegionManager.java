package org.example.theremekk.thewalls.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Map;

public class WGRegionManager {

    private static WGRegionManager instance;
    private WorldGuardPlugin wgPlugin;

    private WGRegionManager() {
    }

    public static WGRegionManager getInstance() {
        if (instance == null) {
            instance = new WGRegionManager();
        }
        return instance;
    }

    public void init(WorldGuardPlugin wgPlugin) {
        if (this.wgPlugin == null) {
            this.wgPlugin = wgPlugin;
        }
    }

    private RegionContainer getRegionContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public ProtectedRegion getRegion(String worldName, String regionName) {
        if (wgPlugin == null) {
            Bukkit.getLogger().warning("WorldGuardPlugin is not initialized!");
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Bukkit.getLogger().warning("World " + worldName + " does not exist!");
            return null;
        }

        RegionContainer container = getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            Bukkit.getLogger().warning("No region manager found for world " + worldName);
            return null;
        }

        return regionManager.getRegion(regionName);
    }

    public void setRegionFlags(String worldName, String regionName, boolean enable) {
        ProtectedRegion region = getRegion(worldName, regionName);
        if (region == null) {
            Bukkit.getLogger().warning("Region " + regionName + " not found in world " + worldName);
            return;
        }

        StateFlag.State state = enable ? StateFlag.State.DENY : null;

        region.setFlag(Flags.ENTRY, state);
        region.setFlag(Flags.BLOCK_BREAK, state);
        region.setFlag(Flags.BLOCK_PLACE, state);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        region.setFlag(Flags.TNT, StateFlag.State.ALLOW);

        Bukkit.getLogger().info("Flags updated for region: " + regionName);
    }

    public void setCustomRegionFlags(String worldName, String regionName, Map<StateFlag, StateFlag.State> stateFlags, Map<DoubleFlag, Double> doubleFlags, Map<IntegerFlag, Integer> intFlags) {
        ProtectedRegion region = getRegion(worldName, regionName);
        if (region == null) {
            Bukkit.getLogger().warning("Region " + regionName + " not found in world " + worldName);
            return;
        }

        // Ustawianie flag typu StateFlag
        if (stateFlags != null) {
            for (Map.Entry<StateFlag, StateFlag.State> entry : stateFlags.entrySet()) {
                region.setFlag(entry.getKey(), entry.getValue());
            }
        }

        // Ustawianie flag typu DoubleFlag (np. heal-max-health)
        if (doubleFlags != null) {
            for (Map.Entry<DoubleFlag, Double> entry : doubleFlags.entrySet()) {
                region.setFlag(entry.getKey(), entry.getValue());
            }
        }

        // Ustawianie flag typu IntegerFlag (np. feed-max-hunger)
        if (intFlags != null) {
            for (Map.Entry<IntegerFlag, Integer> entry : intFlags.entrySet()) {
                region.setFlag(entry.getKey(), entry.getValue());
            }
        }

        Bukkit.getLogger().info("Flagi zostały zaktualizowane dla regionu: " + regionName);
    }

    public boolean createRegion(World world, String regionName, int minX, int minY, int minZ, int maxX, int maxY,
                                int maxZ) {
        if (wgPlugin == null) {
            Bukkit.getLogger().warning("WorldGuardPlugin is not initialized!");
            return false;
        }

        RegionContainer container = getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            Bukkit.getLogger().warning("No region manager found for world ");
            return false;
        }

        if (regionManager.hasRegion(regionName)) {
            Bukkit.getLogger().warning("Region " + regionName + " already exists!");
            return false;
        }

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);
        ProtectedRegion region = new ProtectedCuboidRegion(regionName, min, max);

        regionManager.addRegion(region);

        Bukkit.getLogger().info("Region " + regionName + " created successfully!");
        return true;
    }
}