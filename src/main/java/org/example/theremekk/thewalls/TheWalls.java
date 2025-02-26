package org.example.theremekk.thewalls;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.theremekk.thewalls.commands.LobbyExecutor;
import org.example.theremekk.thewalls.commands.SetlobbyExecutor;
import org.example.theremekk.thewalls.commands.TeamsExecutor;
import org.example.theremekk.thewalls.commands.TheWallsExecutor;
import org.example.theremekk.thewalls.listeners.*;
import org.example.theremekk.thewalls.managers.*;
import org.example.theremekk.thewalls.runnables.ArenaAutoStartRunnable;

public final class TheWalls extends JavaPlugin {
    private WorldGuardPlugin wgPlugin;
    PluginManager manager = getServer().getPluginManager();

    @Override
    public void onEnable() {
        getLogger().info("Plugin stworzony przez gracza TheRemekkPlays!");
        this.wgPlugin = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");

        if (wgPlugin == null) {
            getLogger().severe("WorldGuard nie jest zainstalowany! Plugin nie będzie działał.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ConfigManager configManager = ConfigManager.getInstance();
        configManager.init(this);
        WGRegionManager wgRegionManager = WGRegionManager.getInstance();
        wgRegionManager.init(wgPlugin);
        ArenaManager arenaManager = ArenaManager.getInstance();
        WorldManager worldManager = WorldManager.getInstance();
        worldManager.init(this);
        ScoreboardManager scoreboardManager = ScoreboardManager.getInstance();
        ItemManager itemManager = ItemManager.getInstance();
        GUIManager guiManager = GUIManager.getInstance();

        this.manager.registerEvents(new OnPlayerJoin(this), this);
        this.manager.registerEvents(new OnPlayerQuit(), this);
        this.manager.registerEvents(new OnPlayerChat(), this);
        this.manager.registerEvents(new OnPlayerDeath(), this);
        this.manager.registerEvents(new OnCommandSending(), this);
        this.manager.registerEvents(new OnWorldChange(this), this);
        this.manager.registerEvents(new OnPlayerInteract(),this);
        this.manager.registerEvents(new OnInventoryClick(), this);
        this.manager.registerEvents(new OnBlockBreak(), this);
        this.manager.registerEvents(new OnBlockPlace(this), this);
        this.manager.registerEvents(new OnProjectileLaunch(), this);
        this.manager.registerEvents(new OnDropItem(), this);

        this.getCommand("team").setExecutor(new TeamsExecutor());
        this.getCommand("thewalls").setExecutor(new TheWallsExecutor(this));
        this.getCommand("lobby").setExecutor(new LobbyExecutor());
        this.getCommand("setlobby").setExecutor(new SetlobbyExecutor());

        new ArenaAutoStartRunnable(this).runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin został wyłączony!");
    }
}
