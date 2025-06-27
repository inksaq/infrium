package com.infrium.smpqol.beacon;

import com.infrium.api.database.InfriumDB;
import com.infrium.api.mongoserializer.MongoSerializer;
import com.infrium.core.ICore;
import com.infrium.smpqol.SmpQol;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MongoBeaconRegistry {
    private final SmpQol plugin;
    private final InfriumDB infriumDB;
    private final MongoCollection<Document> collection;
    private final Map<String, BeaconWarp> cache = new ConcurrentHashMap<>();

    public MongoBeaconRegistry(SmpQol plugin) {
        this.plugin = plugin;
        this.infriumDB = ICore.getInstance().getInfriumProvider().getInfriumDB();
        this.collection = infriumDB.getMongoCollection("smp", "beacons");

        // Initial load of beacons into cache
        loadBeacons();
    }

    private void loadBeacons() {
        try {
            collection.find().forEach(document -> {
                BeaconWarp beacon = MongoSerializer.deserialize(document, BeaconWarp.class);
                cache.put(beacon.getId(), beacon);
            });
            plugin.getLogger().info("Loaded " + cache.size() + " beacons from database");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load beacons: " + e.getMessage());
        }
    }

    public void registerBeacon(BeaconWarp beacon) {
        cache.put(beacon.getId(), beacon);
        CompletableFuture.runAsync(() -> {
            try {
                Document doc = MongoSerializer.serialize(beacon);
                collection.replaceOne(
                        Filters.eq("_id", beacon.getId()),
                        doc,
                        new ReplaceOptions().upsert(true)
                );
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to save beacon: " + e.getMessage());
            }
        });
    }

    public void unregisterBeacon(String id) {
        cache.remove(id);
        CompletableFuture.runAsync(() -> {
            try {
                collection.deleteOne(Filters.eq("_id", id));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to delete beacon: " + e.getMessage());
            }
        });
    }

    public Collection<BeaconWarp> getAllBeacons() {
        return Collections.unmodifiableCollection(cache.values());
    }

    public BeaconWarp getBeaconAt(Location location) {
        return cache.values().stream()
                .filter(b -> b.getLocation().equals(location))
                .findFirst()
                .orElse(null);
    }

    // Add method to update a beacon
    public void updateBeacon(BeaconWarp beacon) {
        registerBeacon(beacon); // Uses upsert, so this works for updates too
    }

    // Optional: Add method to find beacons by owner
    public List<BeaconWarp> getBeaconsByOwner(UUID ownerUUID) {
        return cache.values().stream()
                .filter(b -> b.getOwnerUUID().equals(ownerUUID.toString()))
                .collect(Collectors.toList());
    }

    public BeaconWarp getBeaconById(String id) {
        return getAllBeacons().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    //Remove friends from warp
    public void removeFriendFromWarp(String id, UUID friendUUID) {
        BeaconWarp beacon = getBeaconById(id);
        if (beacon == null) {
            return;
        }
        beacon.getFriends().remove(friendUUID.toString());
        updateBeacon(beacon);
    }

    public void addFriendToWarp(String id, UUID friendUUID) {
        BeaconWarp beacon = getBeaconById(id);
        if (beacon == null) {
            return;
        }
        beacon.getFriends().add(friendUUID.toString());
        updateBeacon(beacon);
    }

    //update privacy status
    public void updatePrivacyStatus(String id, BeaconWarp.Privacy privStatus) {
        BeaconWarp beacon = getBeaconById(id);
        if (beacon == null) {
            return;
        }
        beacon.setPrivacy(privStatus);
        updateBeacon(beacon);
    }
}
