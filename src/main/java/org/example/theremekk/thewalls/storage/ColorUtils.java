package org.example.theremekk.thewalls.storage;

import org.bukkit.ChatColor;

public class ColorUtils {
    private static final String prefix = ChatColor.translateAlternateColorCodes('&',"&0&l[&6&lTheWalls&0&l] ");
    private static final String prefixForSb = ChatColor.translateAlternateColorCodes('&',"&6&lTheWalls");

    public static String getPrefix() {
        return prefix;
    }

    public static String getPrefixForSb() {
        return prefixForSb;
    }

    public static String getDefaultColorForSb() {
        return "&a";
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String colorizeWithPrefix(String message) {
        return colorize(getPrefix() + message);
    }

    public static String getTeamPrefix(int team_index) {
        String color = getTeamColor(team_index);
        return "&7[" + color + "Team" + team_index + "&7]";
    }

    public static String getTeamColor(int team_index) {
        if(team_index == 1) return "&a";
        else if (team_index == 2) return "&b";
        else if (team_index == 3) return "&c";
        else if (team_index == 4) return "&e";
        else return null;
    }

}
