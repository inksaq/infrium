package com.infrium.lobby.cosmetic.actives;

import lombok.Getter;
import com.infrium.lobby.InfriumLobby;
import com.infrium.lobby.cosmetic.Cosmetic;
import com.infrium.lobby.cosmetic.CosmeticsManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MidasTouch extends Cosmetic implements Listener {
    private Map<Location, Integer> goldenBlocksDuration = new ConcurrentHashMap<>();
    private Map<Location, Block> originalMaterials = new ConcurrentHashMap<>();
    @Getter
    private final CosmeticsManager cosmeticsManager;
    private final Set<UUID> activeUsers = new HashSet<>();


    public MidasTouch(CosmeticsManager cosmeticsManager) {
        super("Midas Touch", true);
        this.cosmeticsManager = cosmeticsManager;
        this.goldenBlocksDuration = new ConcurrentHashMap<>();
        this.originalMaterials = new ConcurrentHashMap<>();
        startMidasTouchTask();
    }

    @Override
    public void apply(Player player) {
        activeUsers.add(player.getUniqueId());
        this.isActive = true;
    }

    @Override
    public void remove(Player player) {
        activeUsers.remove(player.getUniqueId());
        this.isActive = false;
        revertPlayerBlocks(player);
    }

    @Override
    public void toggle(Player player) {
        super.toggle(player);
    }

    private void revertPlayerBlocks(Player player) {
        List<Location> locationsToRevert = new ArrayList<>();
        for (Map.Entry<Location, Block> entry : originalMaterials.entrySet()) {
            Location loc = entry.getKey();
            if (loc.getWorld().equals(player.getWorld()) && loc.distanceSquared(player.getLocation()) <= 100) { // 10 blocks radius
                Block originalMaterial = entry.getValue();
                loc.getBlock().setType(originalMaterial.getType());
                loc.getBlock().setBlockData(originalMaterial.getBlockData());
                locationsToRevert.add(loc);
            }
        }
        for (Location loc : locationsToRevert) {
            originalMaterials.remove(loc);
            goldenBlocksDuration.remove(loc);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!activeUsers.contains(player.getUniqueId())) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        if (to != null && (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ())) {
            if (isPlayerOnGround(player)) {
                Location blockUnderPlayer = player.getLocation().clone().subtract(0, 0.1, 0);
                Block block = blockUnderPlayer.getBlock();

                if (!block.getType().isAir() && block.getType() != Material.GOLD_BLOCK) {
                    applyMidasTouch(blockUnderPlayer, block, 7);
                }
            }
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!activeUsers.contains(player.getUniqueId())) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block != null && block.getType() != Material.GOLD_BLOCK) {
            applyMidasTouch(block.getLocation(), block, 5);
        }
    }

    private boolean isPlayerOnGround(Player player) {
        Location location = player.getLocation();
        double y = location.getY();
        double nextY = Math.floor(y);

        if (y - nextY < 0.001) { // Check if the player is very close to the next block below
            Block block = location.subtract(0, 0.1, 0).getBlock();
            return !block.isPassable(); // Check if the block below is not passable (i.e., solid)
        }

        return false;
    }

    private void applyMidasTouch(Location location, Block block, int durationSeconds) {
        goldenBlocksDuration.put(location, durationSeconds);
        originalMaterials.put(location, block);
        block.setType(Material.GOLD_BLOCK);
    }

    private void startMidasTouchTask() {
        InfriumLobby.getInstance().getServer().getScheduler().runTaskTimer(InfriumLobby.getInstance(), () -> {
            List<Location> locationsToRemove = new ArrayList<>();

            for (Map.Entry<Location, Integer> entry : goldenBlocksDuration.entrySet()) {
                Location loc = entry.getKey();
                int secondsLeft = entry.getValue() - 1;

                if (secondsLeft <= 0) {
                    Block originalMaterial = originalMaterials.remove(loc);
                    loc.getBlock().setType(originalMaterial.getType());
                    loc.getBlock().setBlockData(originalMaterial.getBlockData());
                    locationsToRemove.add(loc);
                } else {
                    goldenBlocksDuration.put(loc, secondsLeft);
                }
            }

            for (Location loc : locationsToRemove) {
                goldenBlocksDuration.remove(loc);
            }
        }, 20L, 20L);
    }

}
