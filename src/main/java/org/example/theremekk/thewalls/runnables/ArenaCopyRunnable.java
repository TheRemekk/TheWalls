package org.example.theremekk.thewalls.runnables;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.function.Consumer;

public class ArenaCopyRunnable extends BukkitRunnable {
    private final World targetWorld;
    private final List<Block> blocksToCopy;
    private final Consumer<Boolean> callback;
    private int currentIndex = 0;
    private final int batchSize;

    public ArenaCopyRunnable(World targetWorld, List<Block> blocksToCopy, int batchSize, Consumer<Boolean> callback) {
        this.targetWorld = targetWorld;
        this.blocksToCopy = blocksToCopy;
        this.callback = callback;
        this.batchSize = batchSize;
    }

    @Override
    public void run() {
        int copiedBlocks = 0;

        while (currentIndex < blocksToCopy.size() && copiedBlocks < batchSize) {
            Block sourceBlock = blocksToCopy.get(currentIndex);
            Material material = sourceBlock.getType();

            if (material != Material.AIR) {
                BlockData blockData = sourceBlock.getBlockData();
                Block targetBlock = targetWorld.getBlockAt(sourceBlock.getX(), sourceBlock.getY(), sourceBlock.getZ());

                if (targetBlock.getType() != material || !targetBlock.getBlockData().equals(blockData)) {
                    targetBlock.setType(material, false);
                    targetBlock.setBlockData(blockData, false);
                }
            }

            copiedBlocks++;
            currentIndex++;
        }

        if (currentIndex >= blocksToCopy.size()) {
            targetWorld.save();
            callback.accept(true);
            cancel();
        }
    }
}
