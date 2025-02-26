package org.example.theremekk.thewalls.runnables;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.Random;

public class TNTRunnable extends BukkitRunnable {
    private final Location explosionLocation;
    private final World world;
    private final Random random = new Random();

    public TNTRunnable(Location explosionLocation) {
        this.explosionLocation = explosionLocation;
        this.world = explosionLocation.getWorld();
    }

    @Override
    public void run() {
        if (world == null) return;

        world.createExplosion(explosionLocation, 3F, false, false);

        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius / 2; y <= radius / 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = explosionLocation.clone().add(x, y, z).getBlock();
                    if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
                        if (random.nextDouble() < 0.4) { // Mniejsza szansa na wyrzut
                            FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation(), block.getBlockData());
                            fallingBlock.setVelocity(new Vector(
                                    (random.nextDouble() - 0.5) * 1.5,
                                    0.8 + random.nextDouble() * 0.5, // Mniejsza siła wyrzutu w górę
                                    (random.nextDouble() - 0.5) * 1.5
                            ));
                            fallingBlock.setHurtEntities(true);
                        }
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}
