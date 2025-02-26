package org.example.theremekk.thewalls.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.example.theremekk.thewalls.enums.ArenaIngameStage;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.*;
import org.example.theremekk.thewalls.runnables.ArenaRunnable;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TheWallsExecutor implements CommandExecutor {

    private final ConfigManager configManager;
    private final WorldManager worldManager;
    private final ArenaManager arenaManager;
    private final ItemManager itemManager;
    private final Plugin plugin;

    public TheWallsExecutor(Plugin plugin) {
        this.plugin = plugin;
        this.configManager = ConfigManager.getInstance();
        this.arenaManager = ArenaManager.getInstance();
        this.worldManager = WorldManager.getInstance();
        this.itemManager = ItemManager.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)  {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(ColorUtils.colorize(
                        "&3&l=-=-=-= TheWalls - TheRemekkPlays =-=-=-="
                        + "&6\n Plugin stworzony przez TheRemekkPlays"
                        + "&6\n Aby dowiedziec sie wiecej, wpisz ./thewalls help"));
            }
            else if (args.length == 1) {
                if(args[0].equalsIgnoreCase("help")) {
                    if (p.hasPermission("thewalls.help")) {
                        p.sendMessage(ColorUtils.colorize(
                                "&3=-=-=-= TheWalls - TheRemekkPlays =-=-=-="
                                        + "&6\n :: thewalls - Glowna komenda pluginu TheWalls"
                                        + "&e\n  - help - Pomoc pluginu TheWalls"
                                        + "&e\n  - arena - Sluzy do zarzadzania arenami"
                                        + "&7\n  -> list - Wyświetla listę aren"
                                        + "&7\n  -> create <nazwa_areny> - Tworzy arene"
                                        + "&7\n  -> remove <nazwa_areny> - Usuwa arene"
                                        + "&7\n  -> join <nazwa_areny> - Pozwala dołączyć do areny"
                                        + "&7\n  -> leave - Pozwala wyjść z areny"
                                        + "&7\n  -> start <nazwa_areny> - Startuje arene"
                                        + "&e\n  - reload - Przeladowuje plugin"
                                        + "&6\n :: lobby - Teleportuje gracza na lobby"
                                        + "&6\n :: setlobby - Ustawia lobby globalne"
                                        + "&6\n :: team - Wybiera druzyne od <1-4>"));
                    } else p.sendMessage(ColorUtils.colorize(
                            "&3=-=-=-= TheWalls - TheRemekkPlays =-=-=-="
                                    + "&6\n :: thewalls - Glowna komenda pluginu TheWalls"
                                    + "&6\n :: lobby - Teleportuje gracza na lobby"
                                    + "&6\n :: team - Wybiera druzyne od <1-4>"));
                }
                else if(args[0].equalsIgnoreCase("reload")) {
                    if (!p.hasPermission("thewalls.reload")) {
                        p.sendMessage(ColorUtils.colorizeWithPrefix(
                                "&cNie masz uprawnień do użycia tej komendy!"));
                        return true;
                    }
                    configManager.reloadConfig();
                    p.sendMessage(ColorUtils.colorizeWithPrefix(
                            "&7Pomyślnie zaktualizowano!"));
                }
                else if(args[0].equalsIgnoreCase("start")) {
                    p.sendMessage(ColorUtils.colorizeWithPrefix(
                                "&7Musisz podać arenę jaką chcesz wystartować!"));
                }
                else {
                    p.sendMessage(ColorUtils.colorizeWithPrefix(
                            "&7Niepoprawne uzycie komendy, wiecej pod &3/tw help!"));
                }
            }
            else if (args.length == 2) {
                if(args[0].equalsIgnoreCase("arena")) {
                    if (args[1].equalsIgnoreCase("list")) {
                        if (!p.hasPermission("thewalls.arena_list")) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix(
                                    "&cNie masz uprawnień do użycia tej komendy!"));
                            return true;
                        }

                        Collection<Arena> arenas = arenaManager.getArenas().values();
                        List<String> arenaNames = arenas.stream()
                                .map(Arena::getName)
                                .collect(Collectors.toList());

                        if(arenaNames.isEmpty()) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Jeszcze nie utworzono żadnej " +
                                    "areny!"));
                            return true;
                        }

                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Lista istniejacych aren: &3") + arenaNames);
                    }
                    else if(args[1].equalsIgnoreCase("create")) {
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie podano nazwy areny, " +
                                "ktora ma byc stworzona!"));
                    }
                    else if(args[1].equalsIgnoreCase("remove")) {
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie podano nazwy areny, " +
                                "ktora ma byc usunieta!"));
                    }
                    else if(args[1].equalsIgnoreCase("join")) {
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie podano nazwy areny, " +
                                "do ktorej chcesz dolaczyc!"));
                    }
                    else if(args[1].equalsIgnoreCase("leave")) {
                        if(arenaManager.getArenaOfPlayer(p.getUniqueId()) == null) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie znajdujesz się aktualnie na " +
                                    "żadnej arenie !"));
                            return true;
                        }

                        UUID playerUUID = p.getUniqueId();
                        arenaManager.removePlayerCompletely(playerUUID);
                    }
                    else {
                        p.sendMessage(ColorUtils.colorizeWithPrefix(
                                "&7Niepoprawne uzycie komendy, wiecej pod &3/tw help!"));
                    }
                }
                else {
                    p.sendMessage(ColorUtils.colorizeWithPrefix(
                            "&7Niepoprawne uzycie komendy, wiecej pod &3/tw help!"));
                }
            }
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("arena")) {
                    if (args[1].equalsIgnoreCase("create")) {
                        String arenaName = args[2];
                        if (arenaManager.getArena(arenaName) != null) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Arena o podanej nazwie już istnieje!"));
                            return true;
                        }

                        UUID playerUUID = p.getUniqueId();
                        arenaManager.addPlayerToArenaCreation(playerUUID, arenaName);
                        arenaManager.createArena(arenaName);
                        String arenaConfSection = configManager.getArenaConfSection(arenaName);
                        configManager.createSection(arenaConfSection);

                        int defaultMaxPlayersTeam = configManager.getInt("max_players_team");
                        int defaultMinPlayersArena = configManager.getInt("min_players_arena");
                        int defaultMaxPlayersArena = configManager.getInt("max_players_arena");

                        configManager.setInt(arenaConfSection + ".max_players_team", defaultMaxPlayersTeam);
                        configManager.setInt(arenaConfSection + ".min_players_arena", defaultMinPlayersArena);
                        configManager.setInt(arenaConfSection + ".max_players_arena", defaultMaxPlayersArena);
                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Tworzysz arenę o nazwie " +
                                "&3" + args[2] + "&7. Teraz podaj świat na którym powstanie arena."));
                    } else if(args[1].equalsIgnoreCase("start")) {
                        if (!p.hasPermission("thewalls.start")) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix(
                                    "&7Niepoprawne uzycie komendy, wiecej pod &3/tw help!"));
                            return true;
                        }

                        String arenaName = args[2];
                        if (arenaManager.getArena(arenaName) == null) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix(
                                    "&7Arena o podanej nazwie nie istnieje"));
                            return true;
                        }

                        Arena arena = arenaManager.getArena(arenaName);
                        if (arena.getStage() != ArenaStage.ENABLED) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix(
                                    "&7Arena jest na nieodpowiednim etapie! (" + arena.getStage().toString() + ")"));
                            return true;
                        }

                        if (arena.getActivePlayers().size() < 2) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix(
                                    "&7Nie mozna rozpocząć areny jeżeli jest mniej niż 2 graczy!"));
                            return true;
                        }

                        if (arena.getIngameStage() != ArenaIngameStage.WAITING) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix(
                                    "&7Odliczanie już się rozpoczęło!"));
                            return true;
                        }

                        arena.setForceStarted(true);
                        arena.setIngameStage(ArenaIngameStage.STARTING);
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        String arenaName = args[2];
                        if (arenaManager.getArena(arenaName) == null) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix(
                                    "&7Podana arena nie istnieje!"));
                            return true;
                        }

                        p.sendMessage(ColorUtils.colorizeWithPrefix(
                                "&7Pomyslnie usunieto arene o nazwie &3" + args[2] + " &7!"));
                        arenaManager.removeArena(arenaName);
                        configManager.setString(configManager.getArenaConfSection(arenaName), null);
                    } else if (args[1].equalsIgnoreCase("join")) {
                        Arena arena = arenaManager.getArena(args[2]);

                        if (arena == null) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Podana przez ciebie arena nie istnieje!"));
                            return true;
                        }

                        String arenaName = args[2];
                        int maxPlayersArena = configManager.getInt(configManager.getArenaConfSection(arenaName) + ".max_players_arena");
                        UUID playerUUID = p.getUniqueId();

                        if (arena.getStage() != ArenaStage.ENABLED) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Nie możesz teraz dołączyć do areny!"));
                            return true;
                        }

                        if (arena.getActivePlayers().size() >= maxPlayersArena) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Arena jest już pełna!"));
                            return true;
                        }

                        if (arenaManager.getArenaOfPlayer(playerUUID) != null) {
                            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Już należysz do areny, nie możesz dołączyć!"));
                            return true;
                        }

                        arena.addPlayer(playerUUID);
                        ItemStack kits_gui = itemManager.getItem("kits_gui");
                        p.getInventory().setItem(0, kits_gui);

                        String coordsPath = configManager.getArenaCoordsConfSection() + ".lobby";
                        String worldPath = configManager.getArenaConfSection(arenaName);
                        Location arenaLobby = worldManager.getLocation(coordsPath, worldPath);
                        p.teleport(arenaLobby);

                        p.sendMessage(ColorUtils.colorizeWithPrefix("&7Dołączyłeś do areny: &3" + arena.getName() + " &7!"));

                        return true;
                    } else {
                        p.sendMessage(ColorUtils.colorizeWithPrefix(
                                "&7Niepoprawne uzycie komendy, wiecej pod &3/tw help!"));
                    }
                }
                else {
                    p.sendMessage(ColorUtils.colorizeWithPrefix(
                            "&7Niepoprawne uzycie komendy, wiecej pod &3/tw help!"));
                }
            }
        }
        return false;
    }
}
