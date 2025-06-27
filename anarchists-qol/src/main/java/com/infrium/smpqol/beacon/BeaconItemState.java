package com.infrium.smpqol.beacon;

import com.infrium.smpqol.item.QolItemSystem;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BeaconItemState implements QolItemSystem.ItemState {
    private final BeaconWarp beacon;
    private boolean needsUpdate;

    public BeaconItemState(BeaconWarp beacon) {
        this.beacon = beacon;
        this.needsUpdate = false;
    }

    @Override
    public String getType() {
        return "beacon_item";
    }

    @Override
    public boolean needsUpdate() {
        return needsUpdate;
    }

    @Override
    public void update(ItemStack item) {
        if (item == null || beacon == null) return;

        // Store beacon ID in the item
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("beacon_id", beacon.getId().toString());

        // Update other relevant data
        nbtItem.setInteger("beacon_visits", beacon.getVisits());
        nbtItem.setString("beacon_privacy", beacon.getPrivacy().name());

        needsUpdate = false;
    }


    public void markForUpdate() {
        needsUpdate = true;
    }

    public BeaconWarp getBeacon() {
        return beacon;
    }
}