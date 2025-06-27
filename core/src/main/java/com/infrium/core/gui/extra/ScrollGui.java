package com.infrium.core.gui.extra;

import com.infrium.core.gui.AbstractGui;
import com.infrium.core.gui.GuiItem;
import com.infrium.core.item.SimpleItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ScrollGui extends AbstractGui {
    private final List<GuiItem> scrollContent = new ArrayList<>();
    private final Map<Integer, GuiItem> staticItems = new ConcurrentHashMap<>();
    private final int headerSize;
    private final int footerSize;
    private final int contentSize;
    private int scrollOffset = 0;
    private final int rowsPerPage;
    private GuiItem scrollUpButton;
    private GuiItem scrollDownButton;
    private final Map<UUID, Inventory> activeInventories = new ConcurrentHashMap<>();

    /**
     * Creates a new ScrollGui
     * @param size Total inventory size (must be multiple of 9)
     * @param title GUI title
     * @param plugin JavaPlugin instance
     * @param headerSize Number of slots reserved for header
     * @param footerSize Number of slots reserved for footer
     */
    public ScrollGui(int size, Component title, JavaPlugin plugin, int headerSize, int footerSize) {
        super(size, title, plugin);
        this.headerSize = headerSize;
        this.footerSize = footerSize;
        this.contentSize = size - headerSize - footerSize;
        this.rowsPerPage = contentSize / 9;
        setupScrollButtons();
        validateSizes();
    }

    private void validateSizes() {
        if (size % 9 != 0) throw new IllegalArgumentException("Size must be multiple of 9");
        if (headerSize % 9 != 0) throw new IllegalArgumentException("Header size must be multiple of 9");
        if (footerSize % 9 != 0) throw new IllegalArgumentException("Footer size must be multiple of 9");
        if (contentSize < 9) throw new IllegalArgumentException("Content size must be at least 9");
    }

    private void setupScrollButtons() {
        this.scrollUpButton = new GuiItem(size - footerSize,
                new SimpleItemBuilder(Material.ARROW)
                        .name(Component.text("§8⬆ Scroll Up"))
                        .lore(Component.text("§7Click to scroll up")),
                this::handleScrollUp);

        this.scrollDownButton = new GuiItem(size - 1,
                new SimpleItemBuilder(Material.ARROW)
                        .name(Component.text("§8⬇ Scroll Down"))
                        .lore(Component.text("§7Click to scroll down")),
                this::handleScrollDown);
    }

    public void addStaticItem(GuiItem item) {
        if (isValidStaticSlot(item.getSlot())) {
            staticItems.put(item.getSlot(), item);
        } else {
            throw new IllegalArgumentException("Invalid static item slot: " + item.getSlot() +
                    ". Must be in header or footer.");
        }
    }

    private boolean isValidStaticSlot(int slot) {
        return slot < headerSize || slot >= size - footerSize;
    }

    public void addScrollContent(GuiItem item) {
        scrollContent.add(item);
    }

    public void addScrollContents(Collection<GuiItem> items) {
        scrollContent.addAll(items);
    }

    public void clearScrollContent() {
        scrollContent.clear();
        scrollOffset = 0;
    }

    public void clearStaticItems() {
        staticItems.clear();
    }

    public void clearAll() {
        clearScrollContent();
        clearStaticItems();
    }

    private void handleScrollUp(Player player, ItemStack item, Inventory inventory) {
        if (scrollOffset > 0) {
            scrollOffset--;
            updateInventory(player);
        }
    }

    private void handleScrollDown(Player player, ItemStack item, Inventory inventory) {
        if (hasMorePages()) {
            scrollOffset++;
            updateInventory(player);
        }
    }

    private boolean hasMorePages() {
        return (scrollOffset + 1) * contentSize < scrollContent.size();
    }

    public void updateInventory(Player player) {
        Inventory inventory = activeInventories.get(player.getUniqueId());
        if (inventory != null) {
            renderInventory(inventory, player);
        }
    }

    private void renderInventory(Inventory inventory, Player player) {
        // Clear the inventory
        inventory.clear();

        // Render static items (header)
        staticItems.forEach((slot, item) -> {
            if (slot < headerSize) {
                inventory.setItem(slot, item.getItemBuilder().build(player));
            }
        });

        // Render scrollable content
        int startIndex = scrollOffset * (contentSize / 9) * 9;
        int contentSlot = headerSize;

        for (int i = 0; i < contentSize && (startIndex + i) < scrollContent.size(); i++) {
            GuiItem item = scrollContent.get(startIndex + i);
            inventory.setItem(contentSlot + i, item.getItemBuilder().build(player));
        }

        // Render static items (footer)
        staticItems.forEach((slot, item) -> {
            if (slot >= size - footerSize) {
                inventory.setItem(slot, item.getItemBuilder().build(player));
            }
        });

        // Render scroll buttons if needed
        if (scrollOffset > 0) {
            inventory.setItem(scrollUpButton.getSlot(), scrollUpButton.getItemBuilder().build(player));
        }
        if (hasMorePages()) {
            inventory.setItem(scrollDownButton.getSlot(), scrollDownButton.getItemBuilder().build(player));
        }
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        renderInventory(inventory, player);
        player.openInventory(inventory);
        activeInventories.put(player.getUniqueId(), inventory);
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        int slot = inventory.first(clickedItem);
        if (slot == -1) return;

        // Handle static items
        GuiItem staticItem = staticItems.get(slot);
        if (staticItem != null) {
            staticItem.getOnItemClick().onItemClick(player, clickedItem, inventory);
            return;
        }

        // Handle scroll buttons
        if (slot == scrollUpButton.getSlot() && scrollOffset > 0) {
            handleScrollUp(player, clickedItem, inventory);
            return;
        }
        if (slot == scrollDownButton.getSlot() && hasMorePages()) {
            handleScrollDown(player, clickedItem, inventory);
            return;
        }

        // Handle content items
        if (slot >= headerSize && slot < size - footerSize) {
            int contentIndex = (scrollOffset * (contentSize / 9) * 9) + (slot - headerSize);
            if (contentIndex < scrollContent.size()) {
                GuiItem contentItem = scrollContent.get(contentIndex);
                contentItem.getOnItemClick().onItemClick(player, clickedItem, inventory);
            }
        }
    }

    public void removePlayer(Player player) {
        activeInventories.remove(player.getUniqueId());
    }

    public int getCurrentPage() {
        return scrollOffset + 1;
    }

    public int getTotalPages() {
        return Math.max(1, (scrollContent.size() + contentSize - 1) / contentSize);
    }

    public boolean isGuiInventory(Inventory inventory) {
        return activeInventories.containsValue(inventory);
    }
}