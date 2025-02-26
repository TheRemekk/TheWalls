package org.example.theremekk.thewalls.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.example.theremekk.thewalls.storage.ColorUtils;

public class OnCommandSending implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();

        if (command.startsWith("/spawn")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ColorUtils.colorizeWithPrefix("&cNie mozna tego uzywac! Użyj /lobby"));
        }
    }
}
