package com.infrium.smpqol.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QolItemSystem {

    private static final Map<UUID, ItemState> TRACKED_ITEMS = new ConcurrentHashMap<>();
    private final JavaPlugin plugin;

    public QolItemSystem(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public interface ItemState {
        String getType();
        boolean needsUpdate();
        void update(ItemStack item);
    }

    public record ItemMetadata(
            UUID id,
            String type,
            long creationTime,
            String creator
    ) {}

    public QolItemBuilder builder(Material material) {
        return new QolItemBuilder(material);
    }

    public class QolItemBuilder {
        private final Material material;
        private Component name;
        private String type;
        private String creator;
        private ItemState initialState;


        public QolItemBuilder(Material material) {
            this.material = material;
        }

        public QolItemBuilder name(Component name) {
            this.name = name;
            return this;
        }

        public QolItemBuilder type(String type) {
            this.type = type;
            return this;
        }

        public QolItemBuilder creator(String creator) {
            this.creator = creator;
            return this;
        }

        public QolItemBuilder state(ItemState state) {
            this.initialState = state;
            return this;
        }

        public ItemStack build() {
            UUID itemId = UUID.randomUUID();

            ItemStack item = new ItemStack(material);
            if (name != null) {
                item.editMeta(meta -> meta.displayName(name));
            }

            // Apply NBT data
            NBTItem nbtItem = new NBTItem(item);

            // Create our plugin's compound
            var infriumData = nbtItem.addCompound("InfriumData");
            infriumData.setString("id", itemId.toString());
            infriumData.setString("type", type);
            infriumData.setLong("creation_time", System.currentTimeMillis());
            infriumData.setString("creator", creator);

            // If we have an initial state, apply it and track the item
            if (initialState != null) {
                TRACKED_ITEMS.put(itemId, initialState);
                initialState.update(item);
            }

            return nbtItem.getItem();
        }
    }

    // Utility methods
    public Optional<ItemMetadata> getItemMetadata(ItemStack item) {
        if (item == null) return Optional.empty();

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey("InfriumData")) return Optional.empty();

        var data = nbtItem.getCompound("InfriumData");
        return Optional.of(new ItemMetadata(
                UUID.fromString(data.getString("id")),
                data.getString("type"),
                data.getLong("creation_time"),
                data.getString("creator")
        ));
    }

    public Optional<ItemState> getItemState(ItemStack item) {
        return getItemMetadata(item)
                .map(metadata -> TRACKED_ITEMS.get(metadata.id()));
    }

    public boolean isQolItem(ItemStack item) {
        if (item == null) return false;
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey("InfriumData");
    }

    // Update method for scheduler
    public void updateItems() {
        TRACKED_ITEMS.forEach((id, state) -> {
            if (state.needsUpdate()) {
                // Here you would implement logic to find and update the actual item
                // This could involve checking player inventories, etc.
            }
        });
    }
}