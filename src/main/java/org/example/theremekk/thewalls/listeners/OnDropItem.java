package org.example.theremekk.thewalls.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.example.theremekk.thewalls.managers.*;

public class OnDropItem implements Listener {
    private final ItemManager itemManager;

    public OnDropItem() {
        this.itemManager = ItemManager.getInstance();
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack itemDropped = e.getItemDrop().getItemStack();

        for (ItemStack item : itemManager.getRestrictedItems()) {
            if (itemDropped.equals(item)) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
