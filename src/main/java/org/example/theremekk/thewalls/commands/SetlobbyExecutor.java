package org.example.theremekk.thewalls.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.theremekk.thewalls.managers.ConfigManager;
import org.example.theremekk.thewalls.storage.ColorUtils;

public class SetlobbyExecutor implements CommandExecutor {
    private final ConfigManager configManager;

    public SetlobbyExecutor() {
        this.configManager = ConfigManager.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("thewalls.setlobby")) {
                p.sendMessage(ColorUtils.colorizeWithPrefix(
                        "&cNie masz uprawnień do użycia tej komendy!"));
                return true;
            }

            if (args.length == 0) {
                Location playerLoc = p.getLocation();
                configManager.setLocation(configManager.getLobbyLocConfSection(), playerLoc);
                p.sendMessage(ColorUtils.colorizeWithPrefix("&7Pomyślnie ustawiono lobby!"));
            } else {
                p.sendMessage(ColorUtils.colorizeWithPrefix("&7Niepoprawna ilosc argumentow!"));
            }
        }
        return false;
    }
}
