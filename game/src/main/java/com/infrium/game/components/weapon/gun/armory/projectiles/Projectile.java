package com.infrium.game.components.weapon.gun.armory.projectiles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class Projectile {
    protected Particle particle;
    protected double speed;
    protected double damage;
    protected double bulletDrop;  // Amount of downward drop per tick
    protected boolean penetration;  // Whether the bullet can penetrate entities

    public Projectile(Particle particle, double speed, double damage, double bulletDrop, boolean penetration) {
        this.particle = particle;
        this.speed = speed;
        this.damage = damage;
        this.bulletDrop = bulletDrop;
        this.penetration = penetration;
    }

    public void launch(Location from, Vector direction) {
        new BukkitRunnable() {
            Location location = from.clone();
            int range = 100; // arbitrary range value, set this as desired
            double traveled = 0;

            public void run() {
                for (int i = 0; i < speed; i++) {  // "speed" iterations per tick
                    location.add(direction);
                    direction.setY(direction.getY() - 0);  // Apply bullet drop

                    Block block = location.getBlock();
                    if (!block.isPassable()) {  // Bullet hits a block
                        cancel();
                        return;
                    }

                    // Handle entity collision, damaging entities and handling penetration logic
                    // ...

                    // Display the particle
                    location.getWorld().spawnParticle(particle, location, 111);

                    traveled++;
                    if (traveled > range) {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(com.infrium.core.ICore.getInstance(), 0, 1);  // Replace with your main plugin class
    }

    // Getters and potentially other methods
}