package org.example.theremekk.thewalls.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.*;

public class ConfigManager {
    private static ConfigManager instance;
    private Plugin plugin;
    private FileConfiguration config;

    private ConfigManager() {}

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void init(Plugin plugin) {
        if (this.plugin == null) {
            this.plugin = plugin;
            plugin.saveDefaultConfig();
            reloadConfig();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        try {
            plugin.reloadConfig();
            this.config = plugin.getConfig();
            plugin.saveConfig();
            Bukkit.getLogger().info("Plik config.yml został pomyślnie przeładowany!");
        } catch (Exception e) {
            onWarning("Błąd podczas odświeżania pliku konfiguracyjnego!");
        }
    }

    public Integer getInt(String key) {
        try {
            return config.getInt(key);
        } catch (Exception e) {
            onWarning("Błąd podczas odczytywania liczby z config.yml!");
            return 0;
        }
    }

    public Double getDouble(String key) {
        try {
            return config.getDouble(key);
        } catch (Exception e) {
            onWarning("Błąd podczas odczytywania liczby z config.yml!");
            return 0.0;
        }
    }

    public String getString(String key) {
        try {
            return config.getString(key);
        } catch (Exception e) {
            onWarning("Błąd podczas odczytywania string z config.yml!");
            return "";
        }
    }

    public Location getLocation(String key, World world) {
        try {
            double x = getDouble(key + ".x");
            double y = getDouble(key + ".y");
            double z = getDouble(key + ".z");
            float yaw = getDouble(key + ".yaw").floatValue();
            float pitch = getDouble(key + ".pitch").floatValue();

            return new Location(world, x, y, z, yaw, pitch);
        }  catch (Exception e) {
            onWarning("Błąd podczas odczytywania lokalizacji z config.yml!");
            return null;
        }
    }

    public ItemStack getItem(String path) {
        try {
            Material material = Material.valueOf(config.getString(path + ".material", "STONE"));
            int amount = config.getInt(path + ".amount", 1);

            ItemStack item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                if (config.contains(path + ".name")) {
                    meta.setDisplayName(ColorUtils.colorize(config.getString(path + ".name")));
                }
                if (config.contains(path + ".lore")) {
                    List<String> lore = config.getStringList(path + ".lore");
                    meta.setLore(lore);
                }
                item.setItemMeta(meta);
            }
            return item;
        }
        catch (Exception e) {
            onWarning("Błąd podczas odczytywania przedmiotu z config.yml!");
            return null;
        }
    }

    public List<ItemStack> getItemSet(String path) {
        try {
            List<Map<?, ?>> items = config.getMapList(path);
            List<ItemStack> itemSet = new ArrayList<>();

            for (Map<?, ?> itemData : items) {
                Material material = Material.valueOf((String) itemData.get("material"));

                int amount = 1;
                Object amountObj = itemData.get("amount");
                if (amountObj instanceof Number) {
                    amount = ((Number) amountObj).intValue();
                }

                itemSet.add(new ItemStack(material, amount));
            }
            return itemSet;
        } catch (Exception e) {
            onWarning("Błąd podczas odczytywania zestawu przedmiotów z config.yml!");
            return new ArrayList<>();
        }
    }

    public void setInt(String key, int value) {
        config.set(key, value);
        plugin.saveConfig();
    }

    public void setDouble(String key, double value) {
        config.set(key, value);
        plugin.saveConfig();
    }

    public void setString(String key, String value) {
        config.set(key, value);
        plugin.saveConfig();
    }

    public void createSection(String key) {
        config.createSection(key);
        plugin.saveConfig();
    }

    public void setLocation(String key, Location loc) {
        setString(key + ".world", loc.getWorld().getName());
        setDouble(key + ".x", loc.getX());
        setDouble(key + ".y", loc.getY());
        setDouble(key + ".z", loc.getZ());
        setDouble(key + ".yaw", loc.getYaw());
        setDouble(key + ".pitch", loc.getPitch());
    }

    public void setItem(String path, ItemStack item) {
        config.set(path + ".material", item.getType().toString());
        config.set(path + ".amount", item.getAmount());

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                config.set(path + ".name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                config.set(path + ".lore", meta.getLore());
            }
        }

        plugin.saveConfig();
    }

    public void setItemSet(String path, List<ItemStack> itemSet) {
        List<Map<String, Object>> itemList = new ArrayList<>();

        for (ItemStack item : itemSet) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("material", item.getType().toString());
            itemData.put("amount", item.getAmount());
            itemList.add(itemData);
        }

        config.set(path, itemList);
        plugin.saveConfig();
    }

    public String getArenasConfSection() {
        return "arenas";
    }

    public String getArenaConfSection(String arenaName) {
        return getArenasConfSection() + "." + arenaName;
    }

    public String getArenaModelConfSection() {
        return "arena_model";
    }

    public String getLobbyLocConfSection() {
        return "tw_lobby";
    }

    public String getArenaCoordsConfSection() {
        return "arena_coords";
    }

    public String getArenaCountersConfSection() {
        return "arena_counters";
    }

    public String getItemConfSection() {
        return "item";
    }

    public String getItemSetsConfSection() {
        return "item_sets";
    }

    public String getStoneDropConfSection() {
        return "stone_drop";
    }

    public void onError(String errorMessage) {
        Bukkit.getLogger().severe(errorMessage);
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public void onWarning(String warningMessage) {
        Bukkit.getLogger().warning(warningMessage);
    }
}