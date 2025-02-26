package org.example.theremekk.thewalls.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.example.theremekk.thewalls.managers.*;
import org.example.theremekk.thewalls.storage.ColorUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class OnBlockBreak implements Listener {
    private final ArenaManager arenaManager;
    private final ConfigManager configManager;
    int coalExp,ironExp,goldExp,diamondExp,lapisExp,debrisExp,netheriteExp;
    double coalChance,ironChance,goldChance,diamondChance,lapisChance,debrisChance,netheriteChance;
    private final Random random = new Random();


    public OnBlockBreak() {
        this.arenaManager = ArenaManager.getInstance();
        this.configManager = ConfigManager.getInstance();

        coalExp = configManager.getInt(configManager.getStoneDropConfSection()+".coal_exp");
        ironExp = configManager.getInt(configManager.getStoneDropConfSection()+".iron_exp");
        goldExp  = configManager.getInt(configManager.getStoneDropConfSection()+".gold_exp");
        lapisExp  = configManager.getInt(configManager.getStoneDropConfSection()+".lapis_exp");
        diamondExp = configManager.getInt(configManager.getStoneDropConfSection()+".diamond_exp");
        debrisExp  = configManager.getInt(configManager.getStoneDropConfSection()+".debris_exp");
        netheriteExp  = configManager.getInt(configManager.getStoneDropConfSection()+".netherite_exp");

        coalChance = configManager.getDouble(configManager.getStoneDropConfSection()+".coal_chance");
        ironChance = configManager.getDouble(configManager.getStoneDropConfSection()+".iron_chance");
        goldChance = configManager.getDouble(configManager.getStoneDropConfSection()+".gold_chance");
        lapisChance = configManager.getDouble(configManager.getStoneDropConfSection()+".lapis_chance");
        diamondChance = configManager.getDouble(configManager.getStoneDropConfSection()+".diamond_chance");
        debrisChance = configManager.getDouble(configManager.getStoneDropConfSection()+".debris_chance");
        netheriteChance = configManager.getDouble(configManager.getStoneDropConfSection()+".netherite_chance");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        UUID playerUUID = p.getUniqueId();
        Block block = e.getBlock();
        ItemStack mainHand = p.getInventory().getItemInMainHand();
        List<Material> dropBlocks = Arrays.asList(Material.STONE, Material.ANDESITE, Material.GRANITE, Material.DIORITE);
        List<Material> allowedPickaxes = Arrays.asList(Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE);

        if (arenaManager.getArenaOfPlayer(playerUUID) == null) {
            return;
        }

        if (block.getType().toString().endsWith("ORE")) {
            e.setCancelled(true);
            p.sendMessage(ColorUtils.colorizeWithPrefix("&7Przedmioty wypadaja z kamienia!"));
            block.setType(Material.AIR);
            return;
        }

        if (!dropBlocks.contains(block.getType())) {
            return;
        }

        if (!mainHand.getType().toString().endsWith("PICKAXE")) {
            return;
        }

        dropItem(p, Material.COAL, coalChance, coalExp, "&5++ &0Wegiel");
        dropItem(p, Material.IRON_INGOT, ironChance, ironExp, "&5++ &7Zelazo");;
        dropItem(p, Material.GOLD_INGOT, goldChance, goldExp, "&5++ &6Zloto");;
        dropItem(p, Material.LAPIS_LAZULI, lapisChance, lapisExp, "&5++ &9Lapis");
        if (allowedPickaxes.contains(mainHand.getType())) {
            dropItem(p, Material.DIAMOND, diamondChance, diamondExp, "&5++ &bDiament");
            dropItem(p, Material.ANCIENT_DEBRIS, debrisChance, debrisExp, "&5++ &8Ancient debris");
            dropItem(p, Material.NETHERITE_SCRAP, netheriteChance, netheriteExp, "&5++ &6&lNETHERITE!");
        }
    }

    private void dropItem(Player p, Material material, double chance, int exp, String message) {
        Random r = new Random();
        double rangeMin = 0;
        double rangeMax = 100;
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        if(randomValue <= chance) {
            p.getInventory().addItem(new ItemStack(material, 1));
            p.giveExp(exp);
            p.sendMessage(ColorUtils.colorize(message));
        }
    }
}
