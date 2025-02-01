package ltd.bui.infrium.game.components.weapon.workstation.weapon;

import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.FrameBody;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class WeaponWorkstation implements Listener {
    private static final Logger logger = Logger.getLogger(WeaponWorkstation.class.getName());
    private final Plugin plugin;
    private final Map<UUID, Inventory> openWorkstations;

    public WeaponWorkstation(Plugin plugin) {
        this.plugin = plugin;
        this.openWorkstations = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openWorkstation(Player player) {
        Inventory workstation = Bukkit.createInventory(null, 54, "Weapon Workstation");
        setupWorkstationGUI(workstation);
        player.openInventory(workstation);
        openWorkstations.put(player.getUniqueId(), workstation);
        logger.info("Opened workstation for player: " + player.getName());
    }

    private void setupWorkstationGUI(Inventory workstation) {
        workstation.setItem(10, createGuiItem(Material.IRON_BLOCK, "Frame Body"));
        workstation.setItem(19, createGuiItem(Material.REDSTONE_BLOCK, "Charge Cell"));
        workstation.setItem(28, createGuiItem(Material.DIAMOND_BLOCK, "Energy Core"));
        workstation.setItem(37, createGuiItem(Material.EMERALD_BLOCK, "Core Processor"));
        workstation.setItem(25, createGuiItem(Material.GLASS, "Lens Conduit"));
        workstation.setItem(49, createGuiItem(Material.ANVIL, "Assemble Weapon"));
    }

    private ItemStack createGuiItem(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory openedInventory = event.getClickedInventory();

        if (openedInventory != null && openWorkstations.containsValue(openedInventory)) {
            event.setCancelled(true);
            int slot = event.getSlot();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                handleWorkstationInteraction(player, openedInventory, slot, clickedItem);
            }
        }
    }

    private void handleWorkstationInteraction(Player player, Inventory workstation, int slot, ItemStack clickedItem) {
        switch (slot) {
            case 10: // Frame Body
            case 19: // Charge Cell
            case 28: // Energy Core
            case 37: // Core Processor
            case 25: // Lens Conduit
                handleComponentPlacement(player, workstation, slot, clickedItem);
                break;
            case 49: // Assemble Weapon
                assembleWeapon(player, workstation);
                break;
        }
    }

    private void handleComponentPlacement(Player player, Inventory workstation, int slot, ItemStack clickedItem) {
        ItemStack heldItem = player.getItemOnCursor();
        if (heldItem != null && heldItem.getType() != Material.AIR) {
            // TODO: Verify if the held item is a valid component for this slot
            workstation.setItem(slot, heldItem.clone());
            player.setItemOnCursor(null);
            logger.info("Player " + player.getName() + " placed " + heldItem.getType() + " in slot " + slot);
        } else {
            player.getInventory().addItem(clickedItem);
            workstation.setItem(slot, createGuiItem(Material.BARRIER, "Empty Slot"));
            logger.info("Player " + player.getName() + " removed component from slot " + slot);
        }
    }

    private void assembleWeapon(Player player, Inventory workstation) {
        // TODO: Implement weapon assembly logic
        logger.info("Player " + player.getName() + " attempted to assemble a weapon");
        player.sendMessage(ChatColor.YELLOW + "Weapon assembly not yet implemented");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            openWorkstations.remove(player.getUniqueId());
            logger.info("Closed workstation for player: " + player.getName());
        }
    }

    public ItemStack assembleWeapon() {
        ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = weapon.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Weapon");
        weapon.setItemMeta(meta);
        return weapon;
    }
}