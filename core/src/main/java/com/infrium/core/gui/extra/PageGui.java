package com.infrium.core.gui.extra;

import com.infrium.core.gui.AbstractGui;
import com.infrium.core.gui.GuiItem;
import com.infrium.core.gui.ItemBuilder;
import com.infrium.core.item.SimpleItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class PageGui extends AbstractGui {
    private static final int INVENTORY_SIZE = 54; // 6 rows
    private static final int CONTENT_WIDTH = 7;
    private static final int CONTENT_HEIGHT = 3;
    private static final int HEADER_ROW = 0;
    private static final int FOOTER_ROW = 5;

    // Slot constants
    private static final int SORT_BUTTON_SLOT = 2;
    private static final int FILTER_BUTTON_SLOT = 4;
    private static final int SEARCH_BUTTON_SLOT = 6;
    private static final int BACK_BUTTON_SLOT = 45;
    private static final int PAGE_NUMBER_SLOT = 49;
    private static final int NEXT_BUTTON_SLOT = 51;
    private static final int CLOSE_BUTTON_SLOT = 53;

    private final Map<CategoryType, List<GuiItem>> categoryItems = new EnumMap<>(CategoryType.class);
    private CategoryType currentCategory = CategoryType.ALL;
    private SortMode currentSortMode = SortMode.DEFAULT;
    private final boolean showPreviousInventoryButton;
    private int currentPage = 0;
    private Consumer<CategoryType> categoryChangeHandler;
    // GUI elements
    private GuiItem sortButton;
    private GuiItem filterButton;
    private GuiItem searchButton;
    private GuiItem backButton;
    private GuiItem nextButton;
    private GuiItem closeButton;
    private GuiItem borderItem;

    @Getter
    public enum CategoryType {
        OWNED("Owned", Material.CHEST, "§e§lOwned Items", "§7View items you own"),
        DISCOVERED("Discovered", Material.COMPASS, "§b§lDiscovered", "§7Items you've discovered"),
        NEARBY("Nearby", Material.MAP, "§a§lNearby", "§7Items in your vicinity"),
        POPULAR("Popular", Material.DIAMOND, "§d§lPopular", "§7Most popular items"),
        ALL("All", Material.BOOK, "§f§lAll Items", "§7View all available items");

        private final String name;
        private final Material icon;
        private final String displayName;
        private final String description;

        CategoryType(String name, Material icon, String displayName, String description) {
            this.name = name;
            this.icon = icon;
            this.displayName = displayName;
            this.description = description;
        }
    }

    public enum SortMode {
        DEFAULT("Default", Material.HOPPER),
        ALPHABETICAL("Name (A-Z)", Material.NAME_TAG),
        ALPHABETICAL_REVERSE("Name (Z-A)", Material.NAME_TAG),
        NEWEST("Newest First", Material.CLOCK),
        OLDEST("Oldest First", Material.CLOCK);

        final String display;
        final Material icon;

        SortMode(String display, Material icon) {
            this.display = display;
            this.icon = icon;
        }
    }

    public PageGui(Component title, @NonNull JavaPlugin plugin, boolean showPreviousInventoryButton) {
        super(INVENTORY_SIZE, title, plugin);
        this.showPreviousInventoryButton = showPreviousInventoryButton;
        this.borderItem = new GuiItem(0,
                new SimpleItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .name(Component.text("")),
                (player, item, inventory) -> {});

        initializeCategories();
        setupButtons();
    }

    public PageGui(Component title, @NonNull JavaPlugin plugin) {
        this(title, plugin, false);
    }

    private void initializeCategories() {
        for (CategoryType category : CategoryType.values()) {
            categoryItems.put(category, new ArrayList<>());
        }
    }

    public void clearItems(CategoryType category) {
        categoryItems.get(category).clear();
    }

    public void clearAllItems() {
        for (CategoryType category : CategoryType.values()) {
            clearItems(category);
        }
    }

    public List<GuiItem> getCategoryItems(CategoryType category) {
        return categoryItems.get(category);
    }

    private void setupButtons() {
        // Sort button
        this.sortButton = new GuiItem(SORT_BUTTON_SLOT,
                new SimpleItemBuilder(Material.HOPPER)
                        .name(Component.text("§e§lSort Items"))
                        .lore(
                                Component.text(""),
                                Component.text("§7Current: §f" + currentSortMode.display),
                                Component.text(""),
                                Component.text("§e➤ Click to change")
                        ),
                (player, item, inventory) -> openSortMenu(player));

        // Filter button
        this.filterButton = new GuiItem(FILTER_BUTTON_SLOT,
                new SimpleItemBuilder(Material.HOPPER)
                        .name(Component.text("§e§lFilter Items"))
                        .lore(
                                Component.text(""),
                                Component.text("§7Apply filters to your search"),
                                Component.text(""),
                                Component.text("§e➤ Click to filter")
                        ),
                (player, item, inventory) -> {/* Implement filter menu */});

        // Search button
        this.searchButton = new GuiItem(SEARCH_BUTTON_SLOT,
                new SimpleItemBuilder(Material.COMPASS)
                        .name(Component.text("§e§lSearch Items"))
                        .lore(
                                Component.text(""),
                                Component.text("§7Search for specific items"),
                                Component.text(""),
                                Component.text("§e➤ Click to search")
                        ),
                (player, item, inventory) -> {/* Implement search */});

        // Navigation buttons
        this.backButton = new GuiItem(BACK_BUTTON_SLOT,
                new SimpleItemBuilder(Material.ARROW)
                        .name(Component.text(showPreviousInventoryButton ? "§e§lBack" : "§c§lClose")),
                (player, item, inventory) -> {
                    if (showPreviousInventoryButton) {
                        // Implement back navigation
                    } else {
                        player.closeInventory();
                    }
                });

        this.closeButton = new GuiItem(CLOSE_BUTTON_SLOT,
                new SimpleItemBuilder(Material.BARRIER)
                        .name(Component.text("§c§lClose")),
                (player, item, inventory) -> player.closeInventory());
    }

    public void addItem(CategoryType category, GuiItem item) {
        categoryItems.get(category).add(item);
        if (category != CategoryType.ALL) {
            // Also add to ALL category if it's not already an ALL category item
            categoryItems.get(CategoryType.ALL).add(item);
        }
    }

    public void setItems(CategoryType category, List<GuiItem> items) {
        categoryItems.get(category).clear();
        categoryItems.get(category).addAll(items);
    }

    private int getContentStartSlot() {
        return 11; // First slot after category and border
    }

    private int getContentEndSlot() {
        return getContentStartSlot() + (CONTENT_HEIGHT * 9) - 1;
    }

    private int getMaxItemsPerPage() {
        return CONTENT_WIDTH * CONTENT_HEIGHT;
    }

    private int getTotalPages(CategoryType category) {
        return (int) Math.ceil((double) categoryItems.get(category).size() / getMaxItemsPerPage());
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

    private void renderCategories(Inventory inventory, Player player) {
        int slot = 9; // Start from second row
        for (CategoryType category : CategoryType.values()) {
            boolean isSelected = category == currentCategory;

            inventory.setItem(slot,
                    new SimpleItemBuilder(category.icon)
                            .name(Component.text(category.displayName))
                            .lore(
                                    Component.text(""),
                                    Component.text(category.description),
                                    Component.text(""),
                                    Component.text(isSelected ? "§b§lCurrently Selected" : "§e➤ Click to select")
                            )
                            .glow(isSelected)
                            .build(player));

            slot += 9; // Move to next row
        }
    }

    private void renderContent(Inventory inventory, Player player) {
        List<GuiItem> items = categoryItems.get(currentCategory);
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
                slot += 1; // Skip border slots
            }
            slot++;
        }
    }

    private void renderNavigation(Inventory inventory, Player player) {
        // Back/Close button
        inventory.setItem(BACK_BUTTON_SLOT, backButton.getItemBuilder().build(player));

        // Page navigation
        int totalPages = getTotalPages(currentCategory);

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

        if (currentPage < totalPages - 1) {
            inventory.setItem(NEXT_BUTTON_SLOT,
                    new SimpleItemBuilder(Material.ARROW)
                            .name(Component.text("§e§lNext Page"))
                            .build(player));
        }

        // Close button
        inventory.setItem(CLOSE_BUTTON_SLOT, closeButton.getItemBuilder().build(player));
    }

    private void renderHeader(Inventory inventory, Player player) {
        inventory.setItem(SORT_BUTTON_SLOT, sortButton.getItemBuilder().build(player));
        inventory.setItem(FILTER_BUTTON_SLOT, filterButton.getItemBuilder().build(player));
        inventory.setItem(SEARCH_BUTTON_SLOT, searchButton.getItemBuilder().build(player));
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, title);

        renderBorder(inventory);
        renderHeader(inventory, player);
        renderCategories(inventory, player);
        renderContent(inventory, player);
        renderNavigation(inventory, player);

        player.openInventory(inventory);
    }

    private void openSortMenu(Player player) {
        Inventory sortMenu = Bukkit.createInventory(null, 27, Component.text("§e§lSort Items"));

        int slot = 10;
        for (SortMode mode : SortMode.values()) {
            boolean isSelected = mode == currentSortMode;

            ItemStack item = new SimpleItemBuilder(mode.icon)
                    .name(Component.text((isSelected ? "§b§l" : "§e§l") + mode.display))
                    .lore(
                            Component.text(""),
                            Component.text("§7Sort items by " + mode.display.toLowerCase()),
                            Component.text(""),
                            Component.text(isSelected ? "§bCurrently Selected" : "§e➤ Click to select")
                    )
                    .glow(isSelected)
                    .build(player);

            sortMenu.setItem(slot++, item);
        }

        player.openInventory(sortMenu);
    }

    private void openFilterMenu(Player player) {


    }
    protected void onCategoryChange(CategoryType newCategory) {
        if (categoryChangeHandler != null) {
            categoryChangeHandler.accept(newCategory);
        }
    }

    public void setOnCategoryChange(Consumer<CategoryType> handler) {
        this.categoryChangeHandler = handler;
    }


    public void switchCategory(CategoryType category) {
        onCategoryChange(category);
        this.currentCategory = category;
        this.currentPage = 0; // Reset page when switching categories
    }

    public void setSortMode(SortMode mode) {
        this.currentSortMode = mode;
        // Resort items in all categories
        for (Map.Entry<CategoryType, List<GuiItem>> entry : categoryItems.entrySet()) {
            sortCategoryItems(entry.getValue());
        }
    }

    private void sortCategoryItems(List<GuiItem> items) {
        items.sort((item1, item2) -> {
            ItemStack stack1 = item1.getItemBuilder().build(null);
            ItemStack stack2 = item2.getItemBuilder().build(null);
            String name1 = stack1.getItemMeta().getDisplayName();
            String name2 = stack2.getItemMeta().getDisplayName();

            return switch (currentSortMode) {
                case ALPHABETICAL -> name1.compareTo(name2);
                case ALPHABETICAL_REVERSE -> name2.compareTo(name1);
                case NEWEST -> -1; // Implement based on your item data
                case OLDEST -> 1;  // Implement based on your item data
                default -> 0;
            };
        });
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        if (clickedItem == null) return;

        int clickedSlot = inventory.first(clickedItem);
        if (clickedSlot == -1) return;

        // Handle category selection
        if (clickedSlot % 9 == 0 && clickedSlot < 45) {
            int categoryIndex = (clickedSlot / 9) - 1;
            if (categoryIndex >= 0 && categoryIndex < CategoryType.values().length) {
                switchCategory(CategoryType.values()[categoryIndex]);
                openInventory(player);
                return;
            }
        }

        // Handle header buttons
        if (clickedSlot == SORT_BUTTON_SLOT) {
            openSortMenu(player);
            return;
        }
        if (clickedSlot == FILTER_BUTTON_SLOT) {
            // Implement filter menu
            return;
        }
        if (clickedSlot == SEARCH_BUTTON_SLOT) {
            // Implement search
            return;
        }

        // Handle navigation
        if (clickedSlot == BACK_BUTTON_SLOT) {
            backButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
            return;
        }
        if (clickedSlot == CLOSE_BUTTON_SLOT) {
            closeButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
            return;
        }

        // Handle page navigation
        if (clickedSlot == BACK_BUTTON_SLOT + 1 && currentPage > 0) {
            currentPage--;
            openInventory(player);
            return;
        }
        if (clickedSlot == NEXT_BUTTON_SLOT && currentPage < getTotalPages(currentCategory) - 1) {
            currentPage++;
            openInventory(player);
            return;
        }

        // Handle content area clicks
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
        List<GuiItem> items = categoryItems.get(currentCategory);
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