package org.example.theremekk.thewalls.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.enums.ArenaPlayerRoles;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.*;

public class GUIManager {
    private static GUIManager instance;
    private final ArenaManager arenaManager;
    private final ConfigManager configManager;
    private final ItemManager itemManager;
    private final Map<Integer, String> slotToArena = new HashMap<>();
    private final Map<Integer, Player> slotToArenaPlayer = new HashMap<>();
    private final Map<ArenaPlayerRoles, ItemStack> roleItems = new HashMap<>();

    private GUIManager() {
        this.itemManager = ItemManager.getInstance();
        this.arenaManager = ArenaManager.getInstance();
        this.configManager = ConfigManager.getInstance();
        roleItems.put(ArenaPlayerRoles.GORNIK, itemManager.getItem("kit_gornik"));
        roleItems.put(ArenaPlayerRoles.DRWAL, itemManager.getItem("kit_drwal"));
        roleItems.put(ArenaPlayerRoles.WOJOWNIK, itemManager.getItem("kit_wojownik"));
        roleItems.put(ArenaPlayerRoles.LUCZNIK, itemManager.getItem("kit_lucznik"));
    }

    public static GUIManager getInstance() {
        if (instance == null) {
            instance = new GUIManager();
        }
        return instance;
    }

    public Inventory getArenaGui() {
        slotToArena.clear();

        ItemStack item = itemManager.getItem("arena_join");
        ItemStack filler = itemManager.createItem(Material.BLACK_STAINED_GLASS_PANE, 1);
        int size = Math.max(9, ((arenaManager.getArenas().size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, getArenaGuiTitle());

        int i = 0;
        for (Map.Entry<String, Arena> entry : arenaManager.getArenas().entrySet()) {
            String arenaName = entry.getKey();

            Arena arena = arenaManager.getArena(arenaName);
            int maxPlayersArena = configManager.getInt(configManager.getArenaConfSection(arenaName) + ".max_players_arena");

            int arena_number = i+1;
            String arenaStage = arena.getStage().toString();
            if (arena.getIngameStage() == ArenaIngameStage.STARTING) {
                arenaStage = arena.getIngameStage().toString();
            }

            ItemStack arena_join = itemManager.setItemLore(item, ColorUtils.colorize("&7Gracze: &6[" + arena.getActivePlayers().size() + "/" + maxPlayersArena + "]"), ColorUtils.colorize("&7Status areny: &6" + arenaStage));
            arena_join = itemManager.setItemName(arena_join, ColorUtils.colorize("&9#" + arena_number + " (" + arenaName + ")"));

            gui.setItem(i, arena_join);

            for (int j = 0; j < size; j++) {
                if (gui.getItem(j) == null) {
                    gui.setItem(j, filler);
                }
            }

            slotToArena.put(i, arenaName);
            i++;
        }

        return gui;
    }

    public Inventory getKitsGui() {
        Inventory gui = Bukkit.createInventory(null, 9, getKitsGuiTitle());
        ItemStack filler = itemManager.createItem(Material.BLACK_STAINED_GLASS_PANE, 1);
        List<Integer> slots = Arrays.asList(1, 3, 5, 7);
        int index = 0;

        for (Map.Entry<ArenaPlayerRoles, ItemStack> entry : roleItems.entrySet()) {
            if (index >= slots.size()) break;

            int slot = slots.get(index);
            gui.setItem(slot, entry.getValue());

            index++;
        }
        for(int i = 0; i<=8; i++) if(i%2==0) gui.setItem(i, filler);

        return gui;
    }

    public Inventory getArenaPlayersGui(Arena arena) {
        slotToArenaPlayer.clear();

        ItemStack filler = itemManager.createItem(Material.BLACK_STAINED_GLASS_PANE, 1);
        int size = Math.max(9, ((arenaManager.getArenas().size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, getArenaPlayerGuiTitle());

        int i = 0;
        for (UUID playerUUID: arena.getActivePlayers()) {
            if (Bukkit.getPlayer(playerUUID) == null) {
                continue;
            }

            Player p = Bukkit.getPlayer(playerUUID);

            ItemStack arena_player_head = itemManager.getPlayerHead(p, arena);
            gui.setItem(i, arena_player_head);

            for (int j = 0; j < size; j++) {
                if (gui.getItem(j) == null) {
                    gui.setItem(j, filler);
                }
            }

            slotToArenaPlayer.put(i, p);
            i++;
        }

        return gui;
    }


    public String getArenaBySlot(int slot) {
        return slotToArena.get(slot);
    }
    public Player getArenaPlayerBySlot(int slot) {
        return slotToArenaPlayer.get(slot);
    }

    public Map<ArenaPlayerRoles, ItemStack> getRoleItems() {
        return roleItems;
    }

    public String getArenaGuiTitle() {
        return "Wybierz arenę";
    }

    public String getKitsGuiTitle() {
        return "Wybierz zestaw";
    }

    public String getArenaPlayerGuiTitle() {
        return "Obserwuj gracza";
    }

}
