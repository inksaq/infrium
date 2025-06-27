package com.infrium.smpqol.beacon;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BeaconCompassListener implements Listener {
    private final BeaconTracker beaconTracker;
    private final BeaconMapGui mapGui;

    public BeaconCompassListener(BeaconTracker beaconTracker, BeaconMapGui mapGui) {
        this.beaconTracker = beaconTracker;
        this.mapGui = mapGui;
    }

    @EventHandler
    public void onCompassUse(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.COMPASS) return;
        if (!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) return;
        if (!ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).equals("Beacon Tracker")) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        // Right-click to stop tracking
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            beaconTracker.stopTracking(player);
            return;
        }

        // Left-click to open beacon selection GUI
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            openTrackerGui(player);
        }
    }

    private void openTrackerGui(Player player) {
        // Get nearby beacons within 200 blocks
        List<BeaconWarp> nearbyBeacons = beaconTracker.getNearbyBeacons(player, 200);

        int size = Math.min(54, ((nearbyBeacons.size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, "§d§lNearby Beacons");

        for (int i = 0; i < nearbyBeacons.size() && i < size; i++) {
            BeaconWarp beacon = nearbyBeacons.get(i);
            Location beaconLoc = beacon.getLocation();
            double distance = beaconLoc.distance(player.getLocation());

            ItemStack item = new ItemStack(Material.BEACON);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("§b" + (beacon.getName().isEmpty() ? "Unnamed Beacon" : beacon.getName())));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Distance: §f" + String.format("%.1f", distance) + " blocks"));
            lore.add(Component.text("§7Owner: §f" + Bukkit.getOfflinePlayer(UUID.fromString(beacon.getOwnerUUID())).getName()));
            lore.add(Component.text(""));
            lore.add(Component.text("§eClick to track this beacon"));

            meta.lore(lore);
            item.setItemMeta(meta);

            gui.setItem(i, item);
        }

        player.openInventory(gui);
    }
    @EventHandler
    public void onTrackerGuiClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§d§lNearby Beacons")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.BEACON) return;

        // Get nearby beacons again to match with the clicked item
        List<BeaconWarp> nearbyBeacons = beaconTracker.getNearbyBeacons(player, 200);
        String clickedName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        BeaconWarp selectedBeacon;
        if (clickedName.equals("Unnamed Beacon")) {
            // If clicking an unnamed beacon, find the closest beacon
            selectedBeacon = nearbyBeacons.stream()
                    .min((b1, b2) -> {
                        double dist1 = b1.getLocation().distance(player.getLocation());
                        double dist2 = b2.getLocation().distance(player.getLocation());
                        return Double.compare(dist1, dist2);
                    })
                    .orElse(null);
        } else {
            // Otherwise find the beacon with matching name
            selectedBeacon = nearbyBeacons.stream()
                    .filter(b -> clickedName.equals(b.getName()))
                    .findFirst()
                    .orElse(null);
        }

        if (selectedBeacon != null) {
            beaconTracker.startTracking(player, selectedBeacon);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onSortMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§e§lSort Beacons")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.PAPER) return;

//        String sortName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
//        BeaconMapGui.BeaconCategory selectedMode = Arrays.stream(BeaconMapGui.BeaconCategory.values())
//                .filter(mode -> mode.title.equals(sortName))
//                .findFirst()
//                .orElse(BeaconMapGui.BeaconCategory.NEARBY);

        player.closeInventory();
        mapGui.openMap(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only update if the player has moved to a new block
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        NavigatorCompass compass = beaconTracker.getActiveTrackers().get(player.getUniqueId());

        if (compass != null && compass.isTracking()) {
            BeaconWarp beacon = compass.getTrackedBeacon();
            if (beacon != null) {
                beaconTracker.updateCompassTarget(player, beacon.getLocation());
            }
        }
    }
}

