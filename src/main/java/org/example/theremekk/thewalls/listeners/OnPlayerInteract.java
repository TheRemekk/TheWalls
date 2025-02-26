package org.example.theremekk.thewalls.listeners;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.GUIManager;
import org.example.theremekk.thewalls.managers.ItemManager;
import org.example.theremekk.thewalls.storage.Arena;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OnPlayerInteract implements Listener {
    private final ArenaManager arenaManager;
    private final GUIManager guiManager;
    private final ItemManager itemManager;

    public OnPlayerInteract() {
        this.arenaManager = ArenaManager.getInstance();
        this.guiManager = GUIManager.getInstance();
        this.itemManager = ItemManager.getInstance();
    }

    @EventHandler
    void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack itemInMain = p.getInventory().getItemInMainHand();
        Arena arena = arenaManager.getArenaOfPlayer(p.getUniqueId());

        if (arena != null && arena.getStage() == ArenaStage.INGAME) {
            ItemStack arenaPlayersGui = itemManager.getItem("arena_players_gui");
            if (itemInMain.equals(arenaPlayersGui)) {
                e.setCancelled(true);
                p.openInventory(guiManager.getArenaPlayersGui(arena));
                return;
            }
            ItemStack arenaLobby = itemManager.getItem("arena_lobby");
            if (itemInMain.equals(arenaLobby)) {
                e.setCancelled(true);
                arenaManager.removePlayerCompletely(p.getUniqueId());
                return;
            }

            Block block = e.getClickedBlock();
            if (block == null || block.getType() != Material.CHEST) {
                return;
            }

            BlockState state = block.getState();

            if (state instanceof Chest) {
                Chest chest = (Chest) state;
                Inventory inv = chest.getBlockInventory();
                if (inv.contains(Material.BEDROCK)) {
                    Random random = new Random();
                    List<ItemStack> itemMaterials = Arrays.asList(
                            itemManager.createItem(Material.GOLDEN_APPLE, random.nextInt(2) + 1),
                            itemManager.createItem(Material.APPLE, random.nextInt(2) + 1),
                            itemManager.createItem(Material.ENDER_PEARL, random.nextInt(2) + 1),
                            itemManager.createItem(Material.TNT, random.nextInt(2) + 1),
                            itemManager.createItem(Material.IRON_INGOT, random.nextInt(2) + 1),
                            itemManager.createItem(Material.GOLD_INGOT, random.nextInt(2) + 1),
                            itemManager.createItem(Material.DIAMOND, 1)
                    );

                    for (int i = 0; i < 3; i++) {
                        p.getInventory().addItem(itemMaterials.get(random.nextInt(itemMaterials.size())));
                    }

                    inv.remove(Material.BEDROCK);
                    block.setType(Material.AIR);
                }
            }
        }
        else {
            ItemStack thewalls_gui = itemManager.getItem("thewalls_gui");
            if (itemInMain.equals(thewalls_gui)) {
                e.setCancelled(true);
                p.openInventory(guiManager.getArenaGui());
                return;
            }

            ItemStack kits_gui = itemManager.getItem("kits_gui");
            if (itemInMain.equals(kits_gui)) {
                e.setCancelled(true);
                p.openInventory(guiManager.getKitsGui());
                return;
            }
        }
    }
}
