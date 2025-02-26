package org.example.theremekk.thewalls.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.example.theremekk.thewalls.enums.ArenaStage;
import org.example.theremekk.thewalls.managers.ArenaManager;
import org.example.theremekk.thewalls.managers.ConfigManager;
import org.example.theremekk.thewalls.managers.WorldManager;
import org.example.theremekk.thewalls.storage.Arena;
import org.example.theremekk.thewalls.storage.ColorUtils;
import org.example.theremekk.thewalls.storage.Teams;

import java.util.UUID;

public class OnPlayerChat implements Listener {
    private final ArenaManager arenaManager;
    private final WorldManager worldManager;
    private final ConfigManager configManager;

    public OnPlayerChat() {
        this.arenaManager = ArenaManager.getInstance();
        this.worldManager = WorldManager.getInstance();
        this.configManager = ConfigManager.getInstance();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        UUID senderUUID = sender.getUniqueId();

        if (arenaManager.isPlayerCreatingArena(senderUUID)) {
            event.setCancelled(true);
            String targetWorldName = event.getMessage();
            try {
                String arenaName = arenaManager.getNameOfCreatedArena(senderUUID);
                configManager.setString(configManager.getArenaConfSection(arenaName) + ".world", targetWorldName);

                if (worldManager.doesWorldExist(targetWorldName)) {
                    sender.sendMessage(ColorUtils.colorizeWithPrefix("&7Taki świat już istnieje!"));
                } else {
                    sender.sendMessage(ColorUtils.colorizeWithPrefix("&7Trwa tworzenie świata areny..."));
                    worldManager.createEmptyWorld(targetWorldName, targetWorld -> {
                        sender.sendMessage(ColorUtils.colorizeWithPrefix("&7Utworzono świat o nazwie &3" + targetWorldName + "&7!"));
                        sender.sendMessage(ColorUtils.colorizeWithPrefix("&7Teraz trwa kopiowanie modelu areny na świat..."));
                        worldManager.copyArenaModel(targetWorld, isPasted -> {
                            if (isPasted) {
                                sender.teleport(new Location(targetWorld, -6, 81, -6));
                                sender.setGameMode(GameMode.CREATIVE);
                                sender.getInventory().clear();
                                sender.sendMessage(ColorUtils.colorizeWithPrefix("&7Utworzono pomyślnie i przeteleportowano cie do swiata areny!"));
                                worldManager.createWGRegions(targetWorld);
                            }
                            else {
                                sender.sendMessage(ColorUtils.colorizeWithPrefix("&cWystąpił błąd podczas kopiowania modelu areny!"));
                            }
                        });
                    });
                }
                arenaManager.removePlayerFromArenaCreation(senderUUID);
                return;
            } catch (Exception e) {
                configManager.onError("Wystąpił błąd podczas tworzenia pustego świata!");
                return;
            }
        }

        String message = event.getMessage();
        String format = sender.getDisplayName() + ": " + message;

        boolean isGlobalMessage = message.startsWith("!");

        if (isGlobalMessage) {
            message = message.substring(1).trim();
        }

        if (arenaManager.getArenaOfPlayer(senderUUID) == null) {
            return;
        }

        event.getRecipients().clear();
        event.setFormat(format);

        Arena arena = arenaManager.getArenaOfPlayer(senderUUID);
        if (arena.getSpectators().contains(senderUUID)) {
            for (UUID receiverUUID : arena.getSpectators()) {
                Player receiver = Bukkit.getPlayer(receiverUUID);
                if (receiver != null) {
                    event.getRecipients().add(receiver);
                }
            }
        }
        else {
            if (arena.getStage() == ArenaStage.INGAME) {
                Teams team = arena.getTeam();
                int team_index = team.getPlayerTeam(senderUUID);
                event.setFormat(ColorUtils.colorize(ColorUtils.getTeamPrefix(team_index) + sender.getDisplayName() +
                        ": " + message));

                if (isGlobalMessage) {
                    for (UUID receiverUUID : arena.getAllPlayers()) {
                        Player receiver = Bukkit.getPlayer(receiverUUID);
                        if (receiver != null) {
                            event.getRecipients().add(receiver);
                        }
                    }
                } else {
                    for (UUID receiverUUID : team.getTeam(team_index)) {
                        Player receiver = Bukkit.getPlayer(receiverUUID);
                        if (receiver != null) {
                            event.getRecipients().add(receiver);
                        }
                    }
                }
            } else {
                for (UUID receiverUUID : arena.getAllPlayers()) {
                    Player receiver = Bukkit.getPlayer(receiverUUID);
                    if (receiver != null) {
                        event.getRecipients().add(receiver);
                    }
                }
            }
        }
    }
}
