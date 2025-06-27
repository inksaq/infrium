package com.infrium.core.item;

import com.infrium.core.gui.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SimpleItemBuilder implements ItemBuilder {
    private final Material material;

    private int amount = 1;
    private Component name;
    private List<Component> lore = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private List<ItemFlag> flags = new ArrayList<>();
    private boolean unbreakable = false;
    private Integer customModelData;
    private Map<String, PersistentDataEntry<?>> persistentData = new HashMap<>();
    private Function<Player, ItemStack> dynamicBuilder;

    public SimpleItemBuilder(Material material) {
        this.material = material;
    }

    public SimpleItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public SimpleItemBuilder name(Component name) {
        this.name = name;
        return this;
    }

    public SimpleItemBuilder lore(Component... lore) {
        this.lore = List.of(lore);
        return this;
    }

    public SimpleItemBuilder lore(List<Component> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public SimpleItemBuilder addLoreLine(Component line) {
        this.lore.add(line);
        return this;
    }

    public SimpleItemBuilder enchant(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public SimpleItemBuilder flag(ItemFlag... flags) {
        this.flags.addAll(List.of(flags));
        return this;
    }

    public SimpleItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public SimpleItemBuilder modelData(int data) {
        this.customModelData = data;
        return this;
    }

    public <T> SimpleItemBuilder persistentData(JavaPlugin plugin, String key, PersistentDataType<T, T> type, T value) {
        this.persistentData.put(key, new PersistentDataEntry<>(plugin, key, type, value));
        return this;
    }

    public SimpleItemBuilder dynamic(Function<Player, ItemStack> builder) {
        this.dynamicBuilder = builder;
        return this;
    }

    public SimpleItemBuilder glow(boolean glow) {
        if (glow) {
            this.enchant(Enchantment.CHANNELING, 1);
            this.flag(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    @Override
    public ItemStack build(Player player) {
        if (dynamicBuilder != null) {
            return dynamicBuilder.apply(player);
        }

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.displayName(name);
        }

        if (!lore.isEmpty()) {
            meta.lore(lore);
        }

        enchantments.forEach((enchantment, level) ->
                meta.addEnchant(enchantment, level, true));

        flags.forEach(meta::addItemFlags);

        meta.setUnbreakable(unbreakable);

        if (customModelData != null) {
            meta.setCustomModelData(customModelData);
        }

        // Add persistent data
        PersistentDataContainer container = meta.getPersistentDataContainer();
        persistentData.values().forEach(entry -> entry.apply(container));

        item.setItemMeta(meta);
        return item;
    }

    private static class PersistentDataEntry<T> {
        private final JavaPlugin plugin;
        private final String key;
        private final PersistentDataType<T, T> type;
        private final T value;

        public PersistentDataEntry(JavaPlugin plugin, String key, PersistentDataType<T, T> type, T value) {
            this.plugin = plugin;
            this.key = key;
            this.type = type;
            this.value = value;
        }

        public void apply(PersistentDataContainer container) {
            container.set(new org.bukkit.NamespacedKey(plugin, key), type, value);
        }
    }

    // Utility methods for common items
    public static SimpleItemBuilder skull(String texture) {
        return new SimpleItemBuilder(Material.PLAYER_HEAD)
                .persistentData(
                        JavaPlugin.getProvidingPlugin(SimpleItemBuilder.class),
                        "skull_texture",
                        PersistentDataType.STRING,
                        texture
                );
    }

    public static SimpleItemBuilder filler(Material material) {
        return new SimpleItemBuilder(material)
                .name(Component.text(" "))
                .flag(ItemFlag.HIDE_ATTRIBUTES);
    }

    public static SimpleItemBuilder button(Material material, Component name) {
        return new SimpleItemBuilder(material)
                .name(name)
                .flag(ItemFlag.HIDE_ATTRIBUTES);
    }
}
