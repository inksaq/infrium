package com.infrium.lobby.gui;

import com.infrium.core.gui.AbstractGui;
import com.infrium.lobby.InfriumLobby;
import com.infrium.lobby.cosmetic.Cosmetic;
import com.infrium.lobby.cosmetic.CosmeticsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CosmeticSelectorGUI extends AbstractGui {

    private final Inventory inventory;
    private CosmeticsManager cosmeticsManager;

    public CosmeticSelectorGUI(Component title, JavaPlugin plugin, CosmeticsManager cosmeticsManager) {
        super(18, title, plugin);
        this.cosmeticsManager = cosmeticsManager;
        this.inventory = Bukkit.createInventory(null, this.size, this.title);
        buildUI();
    }
    @Override

    public void openInventory(Player player) {
        buildUI();
        player.openInventory(this.inventory);
    }


    public void buildUI() {
        this.inventory.clear();
        this.fill(inventory, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), 0, this.size);

        var cosmetics = new ArrayList<>(InfriumLobby.getInstance().getCosmeticsManager().getAllCosmetics());

        for (Cosmetic cosmetic : cosmetics) {
            try {
                int i = cosmetics.indexOf(cosmetic);
                inventory.setItem(i, craftItem((Player) inventory.getHolder(), cosmetic));
            } catch (Exception e) {
                // ignored exception
            }
        }
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        String cosmeticName = String.valueOf(MiniMessage.miniMessage().escapeTags(ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())));
        Cosmetic cosmetic = InfriumLobby.getInstance().getCosmeticsManager().getCosmeticByName(cosmeticName);
        if (cosmetic != null) {
            if (cosmetic.isUnlocked()) {
                cosmetic.toggle(player);
                player.sendMessage(
                        Component.text("Toggled ", NamedTextColor.GRAY)
                                .append(Component.text(cosmeticName, NamedTextColor.GOLD, TextDecoration.BOLD))
                                .append(Component.text(" - ", NamedTextColor.GRAY))
                                .append(Component.text(cosmetic.isActive() ? "ON" : "OFF",
                                        cosmetic.isActive() ? NamedTextColor.GREEN : NamedTextColor.RED,
                                        TextDecoration.BOLD))
                );
            } else {
                player.sendMessage(
                        Component.text("This cosmetic is ", NamedTextColor.RED)
                                .append(Component.text("locked", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                                .append(Component.text(". Unlock it first!", NamedTextColor.RED))
                );
            }
        }

        player.closeInventory();
    }

    private ItemStack craftItem(Player player, Cosmetic cosmetic) {
        ItemStack item = new ItemStack(Material.ENDER_CHEST); // You can choose a different material for each cosmetic type
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("&c" + cosmetic.getName()));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(cosmetic.isUnlocked() ? "Unlocked" : "Locked"));
        lore.add(Component.text("Click to equip"));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }
}