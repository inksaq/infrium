package com.infrium.smpqol.beacon;

import com.infrium.smpqol.SmpQol;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class BeaconTracker {
    private final SmpQol plugin;
    private final MongoBeaconRegistry beaconRegistry;
    @Getter
    private final Map<UUID, NavigatorCompass> activeTrackers = new HashMap<>();
    private BukkitTask particleTask;

    public BeaconTracker(SmpQol plugin, MongoBeaconRegistry beaconRegistry) {
        this.plugin = plugin;
        this.beaconRegistry = beaconRegistry;
        startParticleTask();
    }

    private void startParticleTask() {
        this.particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            activeTrackers.forEach((playerId, compass) -> {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && compass.isTracking()) {
                    showTrackingParticles(player, compass.getTrackedBeacon());
                }
            });
        }, 0L, 5L); // Run every 5 ticks (1/4 second)
    }

    public void startTracking(Player player, BeaconWarp beacon) {
        if (!beacon.canAccess(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have permission to track this beacon!");
            return;
        }

        NavigatorCompass compass = activeTrackers.computeIfAbsent(player.getUniqueId(),
                k -> new NavigatorCompass(player));
        compass.setTrackedBeacon(beacon);

        // Give player tracking compass if they don't have one
        if (!hasTrackingCompass(player)) {
            giveTrackingCompass(player);
        }

        player.sendMessage(ChatColor.GREEN + "Now tracking: " +
                (beacon.getName().isEmpty() ? "Unnamed Beacon" : beacon.getName()));

        // Update compass target
        updateCompassTarget(player, beacon.getLocation());
    }

    public void stopTracking(Player player) {
        NavigatorCompass compass = activeTrackers.remove(player.getUniqueId());
        if (compass != null) {
            player.sendMessage(ChatColor.YELLOW + "Stopped tracking beacon.");
            resetCompass(player);
        }
    }

    private void showTrackingParticles(Player player, BeaconWarp beacon) {
        if (beacon == null) return;

        Location playerLoc = player.getLocation();
        Location beaconLoc = beacon.getLocation();

        // Only show particles if within 50 blocks
        if (playerLoc.getWorld().equals(beaconLoc.getWorld()) &&
                playerLoc.distance(beaconLoc) <= 50) {

            // Create particle line pointing to beacon
            Vector direction = beaconLoc.toVector().subtract(playerLoc.toVector()).normalize();
            Location particleLoc = playerLoc.clone().add(0, 1.5, 0);

            for (double i = 0; i < 3; i += 0.5) {
                particleLoc.add(direction.clone().multiply(0.5));
                player.spawnParticle(Particle.WITCH, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }

    public void updateCompassTarget(Player player, Location target) {
        NavigatorCompass compass = activeTrackers.get(player.getUniqueId());
        if (compass != null && compass.isTracking()) {
            player.setCompassTarget(target);
        }
    }

    private void resetCompass(Player player) {
        player.setCompassTarget(player.getWorld().getSpawnLocation());
    }

    private boolean hasTrackingCompass(Player player) {
        return player.getInventory().contains(Material.COMPASS);
    }

    private void giveTrackingCompass(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.displayName(Component.text("§d§lBeacon Tracker"));
        meta.lore(Arrays.asList(
                Component.text("§7Use this compass to track"),
                Component.text("§7your selected beacon."),
                Component.text(""),
                Component.text("§eRight-click to stop tracking")
        ));
        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);
    }

    public BeaconWarp findNearestBeacon(Player player) {
        Location playerLoc = player.getLocation();
        return beaconRegistry.getAllBeacons().stream()
                .filter(b -> b.canAccess(player.getUniqueId()))
                .filter(b -> b.getLocation().getWorld().equals(playerLoc.getWorld()))
                .min((b1, b2) -> {
                    double dist1 = b1.getLocation().distance(playerLoc);
                    double dist2 = b2.getLocation().distance(playerLoc);
                    return Double.compare(dist1, dist2);
                })
                .orElse(null);
    }

    public List<BeaconWarp> getNearbyBeacons(Player player, double radius) {
        Location playerLoc = player.getLocation();
        return beaconRegistry.getAllBeacons().stream()
                .filter(b -> b.canAccess(player.getUniqueId()))
                .filter(b -> b.getLocation().getWorld().equals(playerLoc.getWorld()))
                .filter(b -> b.getLocation().distance(playerLoc) <= radius)
                .sorted((b1, b2) -> {
                    double dist1 = b1.getLocation().distance(playerLoc);
                    double dist2 = b2.getLocation().distance(playerLoc);
                    return Double.compare(dist1, dist2);
                })
                .collect(Collectors.toList());
    }

    public void cleanup() {
        if (particleTask != null) {
            particleTask.cancel();
        }
        activeTrackers.clear();
    }

}
