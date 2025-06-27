package com.infrium.game.components.weapon.gun.armory.projectiles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class VillagerParticleProjectile extends Projectile {
    private double traveled = 0; // distance traveled by the projectile
    private final double range = 100; // replace with the desired range
    private final double bulletDropFactor = 0.025; // adjust for more/less drop

    public VillagerParticleProjectile(double speed, double damage, double bulletdrop, boolean penetration) {
        super(Particle.CRIMSON_SPORE, speed, damage, bulletdrop, penetration); // example particle type
    }

    @Override
    public void launch(Location from, Vector direction) {
        new BukkitRunnable() {
            Location location = from.add(direction.normalize().multiply(0.5));
            double travelDistancePerTick = speed / 7; // Assuming 20 ticks per second for Minecraft

            @Override
            public void run() {
                location.add(direction);
                for (int i = 0; i < speed; i++) {  // "speed" iterations per tick
                    location.add(direction);
                    if (traveled <= 30) {
                        direction.setY(direction.getY() - 0.0005);
                    } else if (traveled <= 50) {
                        direction.setY(direction.getY() - 0.0009);
                    } else if (traveled <= 80) {
                        direction.setY(direction.getY() - 0.001);
                    } else {
                        direction.setY(direction.getY() - 0.003);
                    }


                    Block block = location.getBlock();
                    if (!block.isPassable()) {  // Bullet hits a block
                        cancel();
                        return;
                    }

                    // Handle entity collision, damaging entities and handling penetration logic
                    // ...

                    // Display the particle
                    location.getWorld().spawnParticle(particle, location, 1);

                    if (traveled >= range || hitPlayer()) {
                        cancel();
                    }


                    traveled += travelDistancePerTick;
                }
            }
            private boolean hitPlayer() {
                for (Entity entity : location.getWorld().getNearbyEntities(location, 1, 1, 1)) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        double headshotMultiplier = 1.5; // Increase damage for headshots

                        // Check if the shot hit the player's head
                        if (Math.abs(player.getEyeLocation().getY() - location.getY()) < 0.3) {
                            player.damage(damage * headshotMultiplier);
                        } else {
                            player.damage(damage);
                        }

                        // For bullet penetration (e.g., you can use player's armor or block material to reduce the damage)
                        // This is a very basic example, and you'd need to add more logic based on your requirements.
                        damage -= 2; // reducing the damage by 2 for simplicity

                        if (damage <= 0) {
                            return true;
                        }
                    }
                    Creature creature = (Creature) entity;
                    creature.damage(damage);
                }
                return false;
            }

        }.runTaskTimerAsynchronously(com.infrium.core.ICore.getInstance(), 0, 1);
    }
}