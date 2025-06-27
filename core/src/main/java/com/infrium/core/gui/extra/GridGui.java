package com.infrium.core.gui.extra;

import com.infrium.core.gui.AbstractGui;
import com.infrium.core.gui.GuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GridGui extends AbstractGui {
    private final int rows;
    private final int columns;
    private final Map<GridPosition, GuiItem> gridItems = new HashMap<>();
    private GuiItem backButton;

    public GridGui(int rows, Component title, JavaPlugin plugin) {
        super(rows * 9, title, plugin);
        this.rows = rows;
        this.columns = 9;
        setupDefaultButtons();
    }

    private void setupDefaultButtons() {
        this.backButton = new GuiItem(size - 5,
                player -> new ItemStack(Material.BARRIER, 1) {{
                    var meta = getItemMeta();
                    meta.displayName(Component.text("§c§lBack"));
                    setItemMeta(meta);
                }},
                (player, item, inventory) -> player.closeInventory());
    }

    public void setItem(int row, int column, GuiItem item) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        int slot = row * 9 + column;
        item.setSlot(slot);
        gridItems.put(new GridPosition(row, column), item);
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, title);

        // Place grid items
        for (Map.Entry<GridPosition, GuiItem> entry : gridItems.entrySet()) {
            GuiItem item = entry.getValue();
            inventory.setItem(item.getSlot(), item.getItemBuilder().build(player));
        }

        // Add back button
        inventory.setItem(backButton.getSlot(), backButton.getItemBuilder().build(player));

        player.openInventory(inventory);
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        if (clickedItem.equals(backButton.getItemBuilder().build(player))) {
            backButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
            return;
        }

        for (GuiItem item : gridItems.values()) {
            if (clickedItem.equals(item.getItemBuilder().build(player))) {
                item.getOnItemClick().onItemClick(player, clickedItem, inventory);
                break;
            }
        }
    }

    private record GridPosition(int row, int column) {}
}

