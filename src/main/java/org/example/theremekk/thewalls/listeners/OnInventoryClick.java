package org.example.theremekk.thewalls.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.enums.ArenaPlayerRoles;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.*;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.Map;
import java.util.UUID;

public class OnInventoryClick implements Listener {
    private final ArenaManager arenaManager;
    private final GUIManager guiManager;
    private final ItemManager itemManager;
    private final ConfigManager configManager;
    private final WorldManager worldManager;

    public OnInventoryClick() {
        this.arenaManager = ArenaManager.getInstance();
        this.guiManager = GUIManager.getInstance();
        this.itemManager = ItemManager.getInstance();
        this.configManager = ConfigManager.getInstance();
        this.worldManager = WorldManager.getInstance();
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            p.closeInventory();
            return;
        }

        ItemStack kits_gui = itemManager.getItem("kits_gui");
        if (p.getGameMode() != GameMode.CREATIVE) {
            for (ItemStack item : itemManager.getRestrictedItems()) {
                if (clickedItem.equals(item)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        if (e.getView().getTitle().equals(guiManager.getArenaGuiTitle())) {
            e.setCancelled(true);

            int slot = e.getSlot();
            String arenaName = guiManager.getArenaBySlot(slot);
            if (arenaName != null) {
                Arena arena = arenaManager.getArena(arenaName);
                if (arena != null) {
                    int maxPlayersArena = configManager.getInt(configManager.getArenaConfSection(arenaName) + ".max_players_arena");
                    UUID playerUUID = p.getUniqueId();

                    if (arena.getStage() != ArenaStage.ENABLED) {
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie możesz teraz dołączyć do areny!"));
                        return;
                    }

                    if (arena.getActivePlayers().size() >= maxPlayersArena) {
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Arena jest już pełna!"));
                        return;
                    }

                    if (arenaManager.getArenaOfPlayer(playerUUID) != null) {
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Już należysz do areny, nie możesz dołączyć!"));
                        return;
                    }

                    String coordsPath = configManager.getArenaCoordsConfSection() + ".lobby";
                    String worldPath = configManager.getArenaConfSection(arenaName);
                    Location arenaLobby = worldManager.getLocation(coordsPath, worldPath);

                    arena.addPlayer(playerUUID);
                    p.getInventory().setItem(0, kits_gui);
                    p.teleport(arenaLobby);

                    p.sendMessage(ColorUtils.colorizeWithPrefix("&7Dołączyłeś do areny: &3" + arena.getName() + " &7!"));
                }
            }
            p.closeInventory();
        }
        else if (e.getView().getTitle().equals(guiManager.getKitsGuiTitle())) {
            e.setCancelled(true);
            UUID playerUUID = p.getUniqueId();

            Arena arena = arenaManager.getArenaOfPlayer(playerUUID);
            for (Map.Entry<ArenaPlayerRoles, ItemStack> entry : guiManager.getRoleItems().entrySet()) {
                if (clickedItem.isSimilar(entry.getValue())) {
                    arena.setPlayerRole(playerUUID, entry.getKey());
                    p.sendMessage(ColorUtils.colorizeWithPrefix(
                            "&7Wybrales zestaw " + entry.getKey().name().toLowerCase() + "a !"));
                }
            }
            p.closeInventory();
        }

        if (e.getView().getTitle().equals(guiManager.getArenaPlayerGuiTitle())) {
            e.setCancelled(true);

            int slot = e.getSlot();
            Player player = guiManager.getArenaPlayerBySlot(slot);
            if (player != null) {
                p.teleport(player);
                p.sendMessage(ColorUtils.colorizeWithPrefix(
                        "&7Teleportowano cię do gracza: " + player.getDisplayName()));
                p.closeInventory();
            }
        }
    }
}
