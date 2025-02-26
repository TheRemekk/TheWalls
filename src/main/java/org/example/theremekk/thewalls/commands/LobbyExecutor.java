package org.example.theremekk.thewalls.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.ConfigManager;
import org.example.theremekk.thewalls.managers.WorldManager;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.UUID;

public class LobbyExecutor implements CommandExecutor {
    private final ArenaManager arenaManager;

    public LobbyExecutor() {
        this.arenaManager = ArenaManager.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                UUID playerUUID = p.getUniqueId();
                arenaManager.removePlayerCompletely(playerUUID);

                p.sendMessage(ColorUtils.colorizeWithPrefix("&7Zostales przeteleportowany do lobby!"));
            }
            else {
                p.sendMessage(ColorUtils.colorizeWithPrefix("&7Niepoprawna ilosc argumentow!"));
            }
        }
        return false;
    }
}
