package org.example.theremekk.thewalls.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    private final Map<Player, Scoreboard> playerScoreboards = new HashMap<>();
    private static ScoreboardManager instance;
    private final String prefix;

    private ScoreboardManager() {
        prefix = ColorUtils.getPrefixForSb();
    }

    public static ScoreboardManager getInstance() {
        if (instance == null) {
            instance = new ScoreboardManager();
        }
        return instance;
    }

    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("arenaInfo", "dummy", prefix);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(prefix);
        Score score = objective.getScore("");
        score.setScore(0);
        player.setScoreboard(scoreboard);
        playerScoreboards.put(player, scoreboard);
    }

    public void updateScoreboardTitle(Player player, String time, String players) {
        Scoreboard scoreboard = playerScoreboards.get(player);
        if (scoreboard == null) return;

        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) return;

        String color = ColorUtils.getDefaultColorForSb();
        String title =
                ColorUtils.colorize(color + time + " " + prefix + " " + color + players);

        objective.setDisplayName(title);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerScoreboards.remove(player);
    }

    public void clearAll() {
        for (Player player : playerScoreboards.keySet()) {
            removeScoreboard(player);
        }
    }
}
