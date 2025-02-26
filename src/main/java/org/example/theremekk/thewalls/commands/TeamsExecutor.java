package org.example.theremekk.thewalls.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.ConfigManager;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;
import org.example.theremekk.thewalls.storage.Teams;

import java.util.UUID;

public class TeamsExecutor implements CommandExecutor {

    private final ConfigManager configManager;
    private final ArenaManager arenaManager;

    public TeamsExecutor() {
        this.configManager = ConfigManager.getInstance();
        this.arenaManager = ArenaManager.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            UUID playerUUID = p.getUniqueId();

            if(arenaManager.getArenaOfPlayer(playerUUID) == null) {
                p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie znajdujesz sie na arenie!"));
                return true;
            }

            Arena arena = arenaManager.getArenaOfPlayer(playerUUID);

            if(arena.getStage() == ArenaStage.INGAME) {
                p.sendMessage(ColorUtils.colorizeWithPrefix("&7To nie ten etap rozgrywki!"));
                return true;
            }

            String arenaName = arena.getName();
            Teams teams = arena.getTeam();
            int teamIndex;
            int maxPlayersTeam = configManager.getInt(configManager.getArenaConfSection(arenaName) + ".max_players_team");

            if (args.length >= 1) {
                try {
                    teamIndex = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ColorUtils.colorizeWithPrefix("&7Niepoprawny format! Poprawne uzycie: " +
                            "/teams" +
                            " <1-4>"));
                    return true;
                }

                if (!teams.teamExists(teamIndex)) {
                    p.sendMessage(ColorUtils.colorizeWithPrefix("&7Podana druzyna nie istnieje! Poprawne " +
                            "uzycie: /teams <1-4>"));
                    return true;
                } else {
                    if (teams.getPlayerTeam(playerUUID) != null) {
                        int teamIndexOfPlayer = teams.getPlayerTeam(playerUUID);
                        if (teamIndex == teamIndexOfPlayer) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Juz nalezysz do tej druzyny!"));
                            return true;
                        }
                    }
                    if (teams.getTeam(teamIndex).size() < maxPlayersTeam) {
                        if (teams.getPlayerTeam(playerUUID) != null) {
                            int teamIndexOfPlayer = teams.getPlayerTeam(playerUUID);
                            if (teamIndex != teamIndexOfPlayer) teams.removePlayer(teamIndexOfPlayer, playerUUID);
                        }
                        teams.addPlayer(teamIndex, playerUUID);
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Dolaczyles do druzyny " + ColorUtils.getTeamColor(teamIndex) + teamIndex +
                                " [" + teams.getTeam(teamIndex).size() + "/" + maxPlayersTeam + "]"));
                    } else {
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Druzyna numer " + ColorUtils.getTeamColor(teamIndex) + teamIndex + " &7jest" +
                                " juz pelna!"));
                        return true;
                    }
                }
            } else {
                p.sendMessage(ColorUtils.colorizeWithPrefix("&7Brak ustawionego argumentu!"));
                return true;
            }
        }
        return false;
    }
}
