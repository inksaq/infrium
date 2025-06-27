package com.infrium.core.gui.extra;

import com.infrium.core.gui.AbstractGui;
import com.infrium.core.gui.GuiItem;
import com.infrium.core.gui.ItemBuilder;
import com.infrium.core.item.SimpleItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public class TabGui extends AbstractGui {
    private static final int INVENTORY_SIZE = 54; // 6 rows
    private static final int CONTENT_WIDTH = 7;
    private static final int CONTENT_HEIGHT = 4; // One more row than PageGui as we don't have categories
    private static final int HEADER_ROW = 0;
    private static final int FOOTER_ROW = 5;

    // Slot constants
    private static final int PREV_TAB_SLOT = 0;
    private static final int NEXT_TAB_SLOT = 8;
    private static final int BACK_BUTTON_SLOT = 45;
    private static final int PAGE_NUMBER_SLOT = 49;
    private static final int NEXT_BUTTON_SLOT = 51;
    private static final int CLOSE_BUTTON_SLOT = 53;
    private static final int NAVIGATE_BUTTON_SLOT = 47;

    private final Map<String, TabData> tabs = new LinkedHashMap<>();
    private final List<String> tabOrder = new ArrayList<>();
    private String currentTab;
    private int currentPage = 0;
    private final boolean showPreviousInventoryButton;

    // GUI elements
    @Setter private GuiItem backButton;
    @Setter private GuiItem closeButton;
    @Setter private GuiItem navigateButton;
    @Setter private GuiItem previousTabButton;
    @Setter private GuiItem nextTabButton;
    private GuiItem borderItem;

    @Getter
    private static class TabData {
        private final String id;
        private final Component name;
        private final Material icon;
        private final List<GuiItem> content;
        private final boolean glowing;
        private int currentPage;

        public TabData(String id, Component name, Material icon, List<GuiItem> content, boolean glowing) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.content = new ArrayList<>(content);
            this.glowing = glowing;
            this.currentPage = 0;
        }
    }

    public TabGui(Component title, JavaPlugin plugin, boolean showPreviousInventoryButton) {
        super(INVENTORY_SIZE, title, plugin);
        this.showPreviousInventoryButton = showPreviousInventoryButton;
        this.borderItem = new GuiItem(0,
                new SimpleItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .name(Component.text("")),
                (player, item, inventory) -> {});
        setupButtons();
    }

    public TabGui(Component title, JavaPlugin plugin) {
        this(title, plugin, false);
    }

    private void setupButtons() {
        // Tab navigation buttons
        this.previousTabButton = new GuiItem(PREV_TAB_SLOT,
                new SimpleItemBuilder(Material.ARROW)
                        .name(Component.text("§e§lPrevious Tab"))
                        .glow(false),
                (player, item, inventory) -> {
                    int currentIndex = tabOrder.indexOf(currentTab);
                    if (currentIndex > 0) {
                        switchTab(tabOrder.get(currentIndex - 1));
                        openInventory(player);
                    }
                });

        this.nextTabButton = new GuiItem(NEXT_TAB_SLOT,
                new SimpleItemBuilder(Material.ARROW)
                        .name(Component.text("§e§lNext Tab"))
                        .glow(false),
                (player, item, inventory) -> {
                    int currentIndex = tabOrder.indexOf(currentTab);
                    if (currentIndex < tabOrder.size() - 1) {
                        switchTab(tabOrder.get(currentIndex + 1));
                        openInventory(player);
                    }
                });

        // Navigation buttons
        if (showPreviousInventoryButton) {
            this.backButton = new GuiItem(BACK_BUTTON_SLOT,
                    new SimpleItemBuilder(Material.ARROW)
                            .name(Component.text("§e§lPrevious Menu")),
                    (player, item, inventory) -> player.closeInventory());
        } else {
            this.backButton = new GuiItem(BACK_BUTTON_SLOT,
                    new SimpleItemBuilder(Material.BARRIER)
                            .name(Component.text("§c§lClose")),
                    (player, item, inventory) -> player.closeInventory());
        }

        this.closeButton = new GuiItem(CLOSE_BUTTON_SLOT,
                new SimpleItemBuilder(Material.BARRIER)
                        .name(Component.text("§c§lClose")),
                (player, item, inventory) -> player.closeInventory());

        this.navigateButton = new GuiItem(NAVIGATE_BUTTON_SLOT,
                new SimpleItemBuilder(Material.COMPASS)
                        .name(Component.text("§b§lNavigate"))
                        .lore(
                                Component.text(""),
                                Component.text("§7Click to navigate")
                        ),
                (player, item, inventory) -> {/* Implement navigation */});
    }

    public void addTab(String tabId, Component tabName, Material tabIcon, List<GuiItem> content) {
        addTab(tabId, tabName, tabIcon, content, false);
    }

    public void addTab(String tabId, Component tabName, Material tabIcon, List<GuiItem> content, boolean glowing) {
        tabs.put(tabId, new TabData(tabId, tabName, tabIcon, content, glowing));
        tabOrder.add(tabId);
        if (currentTab == null) {
            currentTab = tabId;
        }
    }

    public void switchTab(String tabId) {
        if (tabs.containsKey(tabId)) {
            currentTab = tabId;
            currentPage = tabs.get(tabId).currentPage;
        }
    }

    private int getContentStartSlot() {
        return 10; // First slot after header and border
    }

    private int getContentEndSlot() {
        return getContentStartSlot() + (CONTENT_HEIGHT * 9) - 1;
    }

    private int getMaxItemsPerPage() {
        return CONTENT_WIDTH * CONTENT_HEIGHT;
    }

    private int getTotalPages(TabData tab) {
        return (int) Math.ceil((double) tab.content.size() / getMaxItemsPerPage());
    }

    private void renderBorder(Inventory inventory) {
        // Top border
        fill(inventory, borderItem.getItemBuilder().build(null), 0, 9);

        // Side borders
        for (int row = 1; row < 5; row++) {
            inventory.setItem(row * 9, borderItem.getItemBuilder().build(null));
            inventory.setItem(row * 9 + 8, borderItem.getItemBuilder().build(null));
        }

        // Bottom border
        fill(inventory, borderItem.getItemBuilder().build(null), 45, 54);
    }

    private void renderTabs(Inventory inventory, Player player) {
        // Add tab navigation arrows if needed
        int currentIndex = tabOrder.indexOf(currentTab);
        if (currentIndex > 0) {
            inventory.setItem(PREV_TAB_SLOT,
                    previousTabButton.getItemBuilder().build(player));
        }
        if (currentIndex < tabOrder.size() - 1) {
            inventory.setItem(NEXT_TAB_SLOT,
                    nextTabButton.getItemBuilder().build(player));
        }

        // Calculate center position for tabs
        int availableSpace = 7; // 9 slots minus arrows
        int totalTabs = Math.min(availableSpace, tabOrder.size());
        int startSlot = 1 + (availableSpace - totalTabs) / 2;

        // Add visible tabs
        for (int i = 0; i < totalTabs; i++) {
            TabData tab = tabs.get(tabOrder.get(i));
            boolean isSelected = tab.id.equals(currentTab);

            inventory.setItem(startSlot + i,
                    new SimpleItemBuilder(tab.icon)
                            .name(tab.name)
                            .glow(isSelected || tab.glowing)
                            .lore(
                                    Component.text(""),
                                    Component.text(isSelected ? "§b§lCurrent Tab" : "§e➤ Click to view")
                            )
                            .build(player));
        }
    }

    private void renderContent(Inventory inventory, Player player) {
        TabData currentTabData = tabs.get(currentTab);
        if (currentTabData == null) return;

        List<GuiItem> items = currentTabData.content;
        int startIndex = currentPage * getMaxItemsPerPage();
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(), items.size());

        int slot = getContentStartSlot();
        int itemsPlaced = 0;

        for (int i = startIndex; i < endIndex; i++) {
            GuiItem item = items.get(i);
            item.setSlot(slot);
            inventory.setItem(slot, item.getItemBuilder().build(player));

            itemsPlaced++;
            if (itemsPlaced % CONTENT_WIDTH == 0) {
                slot += 2; // Skip border slots
            }
            slot++;
        }
    }

    private void renderNavigation(Inventory inventory, Player player) {
        // Back/Close button
        inventory.setItem(BACK_BUTTON_SLOT, backButton.getItemBuilder().build(player));

        TabData currentTabData = tabs.get(currentTab);
        if (currentTabData != null) {
            int totalPages = getTotalPages(currentTabData);

            // Previous page button
            if (currentPage > 0) {
                inventory.setItem(BACK_BUTTON_SLOT + 1,
                        new SimpleItemBuilder(Material.ARROW)
                                .name(Component.text("§e§lPrevious Page"))
                                .build(player));
            }

            // Page indicator
            inventory.setItem(PAGE_NUMBER_SLOT,
                    new SimpleItemBuilder(Material.PAPER)
                            .name(Component.text("§7Page " + (currentPage + 1) + "/" + totalPages))
                            .build(player));

            // Next page button
            if (currentPage < totalPages - 1) {
                inventory.setItem(NEXT_BUTTON_SLOT,
                        new SimpleItemBuilder(Material.ARROW)
                                .name(Component.text("§e§lNext Page"))
                                .build(player));
            }
        }

        // Navigate button
        inventory.setItem(NAVIGATE_BUTTON_SLOT, navigateButton.getItemBuilder().build(player));

        // Close button
        inventory.setItem(CLOSE_BUTTON_SLOT, closeButton.getItemBuilder().build(player));
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, title);

        renderBorder(inventory);
        renderTabs(inventory, player);
        renderContent(inventory, player);
        renderNavigation(inventory, player);

        player.openInventory(inventory);
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        if (clickedItem == null) return;

        int clickedSlot = inventory.first(clickedItem);
        if (clickedSlot == -1) return;

        // Handle tab navigation
        if (clickedSlot == PREV_TAB_SLOT || clickedSlot == NEXT_TAB_SLOT) {
            int currentIndex = tabOrder.indexOf(currentTab);
            if (clickedSlot == PREV_TAB_SLOT && currentIndex > 0) {
                previousTabButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
                return;
            }
            if (clickedSlot == NEXT_TAB_SLOT && currentIndex < tabOrder.size() - 1) {
                nextTabButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
                return;
            }
        }

        // Handle tab selection
        if (clickedSlot >= 1 && clickedSlot <= 7) {
            int totalTabs = Math.min(7, tabOrder.size());
            int startSlot = 1 + (7 - totalTabs) / 2;
            int tabIndex = clickedSlot - startSlot;

            if (tabIndex >= 0 && tabIndex < tabOrder.size()) {
                switchTab(tabOrder.get(tabIndex));
                openInventory(player);
                return;
            }
        }

        // Handle navigation buttons
        if (clickedSlot == BACK_BUTTON_SLOT) {
            backButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
            return;
        }
        if (clickedSlot == CLOSE_BUTTON_SLOT) {
            closeButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
            return;
        }
        if (clickedSlot == NAVIGATE_BUTTON_SLOT) {
            navigateButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
            return;
        }

        // Handle page navigation
        TabData currentTabData = tabs.get(currentTab);
        if (currentTabData != null) {
            if (clickedSlot == BACK_BUTTON_SLOT + 1 && currentPage > 0) {
                currentPage--;
                currentTabData.currentPage = currentPage;
                openInventory(player);
                return;
            }
            if (clickedSlot == NEXT_BUTTON_SLOT && currentPage < getTotalPages(currentTabData) - 1) {
                currentPage++;
                currentTabData.currentPage = currentPage;
                openInventory(player);
                return;
            }
        }

        // Handle content clicks
        if (isContentSlot(clickedSlot)) {
            handleContentClick(player, clickedItem, inventory, clickedSlot);
        }
    }

    private boolean isContentSlot(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        return row >= 1 && row <= 4 && col >= 1 && col <= 7;
    }

    private void handleContentClick(Player player, ItemStack clickedItem, Inventory inventory, int clickedSlot) {
        TabData currentTabData = tabs.get(currentTab);
        if (currentTabData == null) return;

        List<GuiItem> items = currentTabData.content;
        int startIndex = currentPage * getMaxItemsPerPage();

        // Calculate the relative position in the content area
        int relativeSlot = clickedSlot - getContentStartSlot();
        int row = relativeSlot / 9;
        int col = relativeSlot % 9;

        // Convert to index in items list
        int itemIndex = startIndex + (row * CONTENT_WIDTH) + col;

        if (itemIndex >= 0 && itemIndex < items.size()) {
            GuiItem item = items.get(itemIndex);
            item.getOnItemClick().onItemClick(player, clickedItem, inventory);
        }
    }
}