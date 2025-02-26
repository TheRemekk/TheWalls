package org.example.theremekk.thewalls.managers;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;
import org.example.theremekk.thewalls.storage.Teams;

import java.util.*;

public class ItemManager {
    private static ItemManager instance;
    private final ConfigManager configManager;
    private final Map<String, ItemStack> items = new HashMap<>();
    private final Map<String, List<ItemStack>> itemSets = new HashMap<>();
    private final List<ItemStack> restrictedItems = new ArrayList<>();

    private ItemManager() {
        this.configManager = ConfigManager.getInstance();
        ItemStack thewalls_gui = getItem("thewalls_gui");
        ItemStack kits_gui = getItem("kits_gui");
        ItemStack arenaPlayersGui = getItem("arena_players_gui");
        ItemStack arenaLobbyItem = getItem("arena_lobby");
        restrictedItems.add(thewalls_gui);
        restrictedItems.add(kits_gui);
        restrictedItems.add(arenaPlayersGui);
        restrictedItems.add(arenaLobbyItem);
    }

    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    public ItemStack createItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    public void addItem(String key, ItemStack item) {
        items.put(key, item);
        configManager.getItem(key);
    }

    public void addItemSet(String key, List<ItemStack> itemSet) {
        itemSets.put(key, itemSet);
        configManager.getItemSet(key);
    }

    public ItemStack getItem(String key) {
        return items.computeIfAbsent(key, k -> configManager.getItem(configManager.getItemConfSection() + "." + k));
    }

    public List<ItemStack> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<ItemStack> getItemSet(String key) {
        return itemSets.computeIfAbsent(key,
                k -> configManager.getItemSet(configManager.getItemSetsConfSection() + "." + k));
    }

    public ItemStack setItemName(ItemStack item, String itemName) {
        if (item == null || !item.hasItemMeta()) return item;

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack setItemLore(ItemStack item, String... newLore) {
        if (item == null || !item.hasItemMeta()) return item;

        ItemMeta meta = item.getItemMeta();
        List<String> lore = Arrays.asList(newLore);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public List<ItemStack> getRestrictedItems() {
        return restrictedItems;
    }

    public ItemStack getPlayerHead(Player player, Arena arena) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(player);
            Teams teams = arena.getTeam();

            meta.setDisplayName(ColorUtils.colorize(ColorUtils.getTeamPrefix(teams.getPlayerTeam(player.getUniqueId()))
                    + player.getDisplayName()));
            skull.setItemMeta(meta);
        }

        return skull;
    }
}