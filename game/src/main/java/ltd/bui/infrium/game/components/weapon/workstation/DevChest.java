package ltd.bui.infrium.game.components.weapon.workstation;

import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentLogger;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.ICoreComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class DevChest implements Listener {
    private static final String LOG_PREFIX = "EMPTY";
    private final Map<UUID, List<ItemStack>> playerChests = new HashMap<>();
    private final Map<UUID, Inventory> openInventories = new HashMap<>();
    private final Plugin plugin;

    public DevChest(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        CoreComponentLogger.info(CoreComponentType.valueOf(LOG_PREFIX), "DevChest system initialized");
    }

    public void addItem(Player player, ItemStack item) {
        UUID playerId = player.getUniqueId();
        playerChests.computeIfAbsent(playerId, k -> new ArrayList<>()).add(item.clone());
        CoreComponentLogger.info(CoreComponentType.valueOf(LOG_PREFIX), "Added item to " + player.getName() + "'s DevChest: " + item.getType());
    }

    public List<ItemStack> getPlayerItems(UUID playerId) {
        return playerChests.getOrDefault(playerId, new ArrayList<>());
    }

    public ItemStack removeItem(Player player, int index) {
        UUID playerId = player.getUniqueId();
        List<ItemStack> items = playerChests.get(playerId);
        if (items != null && index >= 0 && index < items.size()) {
            ItemStack removedItem = items.remove(index);
            CoreComponentLogger.info(CoreComponentType.valueOf(LOG_PREFIX), "Removed item from " + player.getName() + "'s DevChest: " + removedItem.getType());
        }
        return null;
    }

    public void openDevChest(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory(null, 54, "Dev Chest - " + player.getName());
        List<ItemStack> items = getPlayerItems(playerId);

        for (int i = 0; i < Math.min(items.size(), 54); i++) {
            inventory.setItem(i, items.get(i));
        }

        player.openInventory(inventory);
        openInventories.put(playerId, inventory);
        CoreComponentLogger.info(CoreComponentType.valueOf(LOG_PREFIX), player.getName() + " opened their DevChest");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && openInventories.containsValue(clickedInventory)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                player.getInventory().addItem(clickedItem.clone());
                removeItem(player, event.getSlot());
                clickedInventory.setItem(event.getSlot(), null);
                CoreComponentLogger.info(CoreComponentType.valueOf(LOG_PREFIX), player.getName() + " retrieved " + clickedItem.getType() + " from their DevChest");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            openInventories.remove(player.getUniqueId());
            CoreComponentLogger.info(CoreComponentType.valueOf(LOG_PREFIX), player.getName() + " closed their DevChest");
        }
    }

    public void tickItems() {
        for (List<ItemStack> items : playerChests.values()) {
            for (ItemStack item : items) {
                if (item != null) {
                    CoreComponent component = ICoreComponent.fromItemStack(item);
                    if (component != null) {
                        component.onTick();
                        // Update the ItemStack with the ticked component
                        ItemStack updatedItem = ICoreComponent.createItemStack();
                        int index = items.indexOf(item);
                        items.set(index, updatedItem);
                    }
                }
            }
        }
        CoreComponentLogger.debug(CoreComponentType.valueOf(LOG_PREFIX), "Ticked all items in DevChests");
    }

    public void displayContents(Player player) {
        UUID playerId = player.getUniqueId();
        List<ItemStack> items = getPlayerItems(playerId);
        player.sendMessage(ChatColor.YELLOW.toString() + "Your DevChest Contents:");
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            String itemName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString();
            player.sendMessage(ChatColor.GRAY.toString() + (i + 1) + ". " + ChatColor.WHITE.toString() + itemName);
        }
    }
}