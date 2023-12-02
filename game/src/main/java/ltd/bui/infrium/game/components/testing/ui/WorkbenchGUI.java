package ltd.bui.infrium.game.components.testing.ui;

import ltd.bui.infrium.core.gui.AbstractGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class WorkbenchGUI extends AbstractGui {
    public WorkbenchGUI(Component title, JavaPlugin plugin) {
        super(27, title, plugin); // Assuming a 3x9 inventory for the workbench
    }
    @Override
    public void openInventory(Player player) {
        Inventory inv = Bukkit.createInventory(player, size, title);
        // Initialize the inventory with slots for weapon, components, and upgrades
        initializeInventory(inv);
        player.openInventory(inv);

    }

    private void initializeInventory(Inventory inv) {
        // Logic to initialize the inventory with slots
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        // Logic for handling item clicks in the inventory
    }
}