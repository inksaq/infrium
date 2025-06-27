package com.infrium.core.gui.extra;

import com.infrium.core.gui.AbstractGui;
import com.infrium.core.gui.GuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BorderGui extends AbstractGui {
    private final List<GuiItem> borderItems = new ArrayList<>();
    private final List<GuiItem> centerItems = new ArrayList<>();
    private final List<Integer> borderSlots;
    private final List<Integer> centerSlots;

    public BorderGui(int size, Component title, JavaPlugin plugin) {
        super(size, title, plugin);
        this.borderSlots = calculateBorderSlots();
        this.centerSlots = calculateCenterSlots();
    }

    private List<Integer> calculateBorderSlots() {
        List<Integer> slots = new ArrayList<>();
        int rows = size / 9;

        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            slots.add(i);  // Top
            slots.add(size - 9 + i);  // Bottom
        }

        // Side columns (excluding corners)
        for (int row = 1; row < rows - 1; row++) {
            slots.add(row * 9);  // Left
            slots.add(row * 9 + 8);  // Right
        }

        return slots;
    }

    private List<Integer> calculateCenterSlots() {
        List<Integer> slots = new ArrayList<>();
        int rows = size / 9;

        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < 8; col++) {
                slots.add(row * 9 + col);
            }
        }

        return slots;
    }

    @Override
    public void openInventory(Player player) {

    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {

    }

    // ... rest of BorderGui implementation
}