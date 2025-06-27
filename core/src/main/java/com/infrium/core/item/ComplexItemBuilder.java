package com.infrium.core.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class ComplexItemBuilder {

    // Core interfaces for item behavior
    public interface ItemDataType<T> {
        void serialize(PersistentDataContainer container, NamespacedKey key, T value);
        T deserialize(PersistentDataContainer container, NamespacedKey key);
        boolean isValid(T value);
        Class<T> getDataClass();
    }

    public interface ItemUpdater {
        void tick(ItemStack item, PersistentDataContainer container);
        int getUpdateInterval();
        boolean shouldUpdate(ItemStack item);
        void onEquip(Player player, ItemStack item);
        void onUnequip(Player player, ItemStack item);
    }

    public interface StateChangeListener<T> {
        void onStateChanged(ItemStack item, T oldState, T newState);
    }

    // Event system for state changes
    private static class ItemStateManager {
        private static final Map<UUID, Map<Class<?>, List<StateChangeListener<?>>>> listeners = new ConcurrentHashMap<>();

        public static <T> void addListener(UUID itemId, Class<T> stateClass, StateChangeListener<T> listener) {
            listeners.computeIfAbsent(itemId, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(stateClass, k -> new ArrayList<>())
                    .add(listener);
        }

        @SuppressWarnings("unchecked")
        public static <T> void notifyStateChange(UUID itemId, Class<T> stateClass, T oldState, T newState) {
            Optional.ofNullable(listeners.get(itemId))
                    .map(m -> m.get(stateClass))
                    .ifPresent(l -> l.forEach(listener ->
                            ((StateChangeListener<T>) listener).onStateChanged(null, oldState, newState)));
        }
    }

    // Cache system for performance
    private static class ItemDataCache {
        private static final Cache<UUID, Map<String, Object>> DATA_CACHE = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build();

        public static <T> void cacheData(UUID itemId, String key, T value) {
            DATA_CACHE.getIfPresent(itemId).put(key, value);
        }

        @SuppressWarnings("unchecked")
        public static <T> Optional<T> getCachedData(UUID itemId, String key, Class<T> type) {
            return Optional.ofNullable(DATA_CACHE.getIfPresent(itemId))
                    .map(m -> (T) m.get(key));
        }
    }

    // Registry for custom data types
    private static class DataTypeRegistry {
        private static final Map<String, ItemDataType<?>> REGISTRY = new ConcurrentHashMap<>();

        public static <T> void register(String key, ItemDataType<T> dataType) {
            REGISTRY.put(key, dataType);
        }

        @SuppressWarnings("unchecked")
        public static <T> Optional<ItemDataType<T>> get(String key) {
            return Optional.ofNullable((ItemDataType<T>) REGISTRY.get(key));
        }
    }

    // Update handling system
    private static class UpdateHandler implements Listener {
        private static final Map<UUID, ItemUpdater> UPDATING_ITEMS = new ConcurrentHashMap<>();
        private static final Map<UUID, Long> LAST_UPDATE = new ConcurrentHashMap<>();

        public static void registerUpdatingItem(UUID itemId, ItemUpdater updater) {
            UPDATING_ITEMS.put(itemId, updater);
            LAST_UPDATE.put(itemId, System.currentTimeMillis());
        }

        public static void unregisterUpdatingItem(UUID itemId) {
            UPDATING_ITEMS.remove(itemId);
            LAST_UPDATE.remove(itemId);
        }

        public static void tickAll() {
            long currentTime = System.currentTimeMillis();
            UPDATING_ITEMS.forEach((itemId, updater) -> {
                long lastUpdate = LAST_UPDATE.getOrDefault(itemId, 0L);
                if (currentTime - lastUpdate >= updater.getUpdateInterval() * 50) { // Convert ticks to ms
                    // Implementation for actual update logic
                    LAST_UPDATE.put(itemId, currentTime);
                }
            });
        }

        @EventHandler
        public void onItemHeld(PlayerItemHeldEvent event) {
            ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
            ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());

            handleItemSwitch(event.getPlayer(), oldItem, newItem);
        }

        private void handleItemSwitch(Player player, @Nullable ItemStack oldItem, @Nullable ItemStack newItem) {
            if (oldItem != null) {
                getItemUpdater(oldItem).ifPresent(updater -> updater.onUnequip(player, oldItem));
            }
            if (newItem != null) {
                getItemUpdater(newItem).ifPresent(updater -> updater.onEquip(player, newItem));
            }
        }

        private Optional<ItemUpdater> getItemUpdater(ItemStack item) {
            return Optional.ofNullable(item.getItemMeta())
                    .map(ItemMeta::getPersistentDataContainer)
                    .map(container -> container.get(
                            new NamespacedKey(JavaPlugin.getProvidingPlugin(ComplexItemBuilder.class), "item_id"),
                            PersistentDataType.STRING
                    ))
                    .map(UUID::fromString)
                    .map(UPDATING_ITEMS::get);
        }
    }

    // Builder fields
    private final JavaPlugin plugin;
    private final Material material;
    private final UUID itemId;
    private final Map<String, Object> complexData;
    private ItemUpdater itemUpdater;
    private Component name;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;
    private Set<ItemFlag> flags;
    private boolean unbreakable;
    private Integer customModelData;
    private Map<String, Consumer<PersistentDataContainer>> persistentDataSetters;

    public ComplexItemBuilder(@NotNull JavaPlugin plugin, @NotNull Material material) {
        this.plugin = plugin;
        this.material = material;
        this.itemId = UUID.randomUUID();
        this.complexData = new HashMap<>();
        this.lore = new ArrayList<>();
        this.enchantments = new HashMap<>();
        this.flags = new HashSet<>();
        this.persistentDataSetters = new HashMap<>();
    }

    // Builder methods
    public ComplexItemBuilder name(@NotNull Component name) {
        this.name = name;
        return this;
    }

    public ComplexItemBuilder lore(@NotNull Component... lore) {
        this.lore = new ArrayList<>(Arrays.asList(lore));
        return this;
    }

    public ComplexItemBuilder addLoreLine(@NotNull Component line) {
        this.lore.add(line);
        return this;
    }

    public ComplexItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ComplexItemBuilder flags(@NotNull ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public ComplexItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ComplexItemBuilder modelData(int data) {
        this.customModelData = data;
        return this;
    }

    public <T> ComplexItemBuilder withComplexData(@NotNull String key, @NotNull T value, @NotNull ItemDataType<T> dataType) {
        if (!dataType.isValid(value)) {
            plugin.getLogger().warning("Invalid data provided for key: " + key);
            return this;
        }
        complexData.put(key, value);
        DataTypeRegistry.register(key, dataType);
        return this;
    }

    public ComplexItemBuilder withUpdater(@NotNull ItemUpdater updater) {
        this.itemUpdater = updater;
        return this;
    }

    public <T> ComplexItemBuilder addStateChangeListener(@NotNull Class<T> stateClass, @NotNull StateChangeListener<T> listener) {
        ItemStateManager.addListener(itemId, stateClass, listener);
        return this;
    }

    // Build method
    public ItemStack build() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Apply basic metadata
        if (name != null) meta.displayName(name);
        if (!lore.isEmpty()) meta.lore(lore);
        enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        flags.forEach(meta::addItemFlags);
        meta.setUnbreakable(unbreakable);
        if (customModelData != null) meta.setCustomModelData(customModelData);

        // Apply persistent data
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, "item_id"), PersistentDataType.STRING, itemId.toString());

        // Apply complex data
        complexData.forEach((key, value) -> DataTypeRegistry.get(key).ifPresent(dataType ->
                applyComplexData(container, key, value, dataType)));

        // Register updater if present
        if (itemUpdater != null) {
            UpdateHandler.registerUpdatingItem(itemId, itemUpdater);
        }

        item.setItemMeta(meta);
        return item;
    }

    @SuppressWarnings("unchecked")
    private <T> void applyComplexData(PersistentDataContainer container, String key, Object value, ItemDataType<?> dataType) {
        try {
            ((ItemDataType<T>) dataType).serialize(container, new NamespacedKey(plugin, key), (T) value);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error applying complex data for key: " + key, e);
        }
    }
}