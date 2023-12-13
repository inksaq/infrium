package ltd.bui.infrium.game.components.testing.ui;

import ltd.bui.infrium.core.gui.AbstractGui;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.registry.WeaponRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorkbenchGUI extends AbstractGui {
    private FrameBody fb;
    public WorkbenchGUI(Component title, JavaPlugin plugin) {
        super(45, title, plugin); // Assuming a 3x9 inventory for the workbench
    }

    @Override
    public void openInventory(Player player) {
        fb = WeaponRegistry.getInstance().getFrameBody(player.getInventory().getItemInMainHand());
        Inventory inv = Bukkit.createInventory(player, size, title);

        // Initialize the inventory with slots for weapon, components, and upgrades
        initializeInventory(inv);

        player.openInventory(inv);

    }


    private void initializeInventory(Inventory inv) {
        // Assuming FrameBody `fb` has already been set
        if (fb == null) return;

        // Specific slots for each component
        int frameBodySlot = 19;  // Example slot index for Frame Body
        int chargeCellSlot = 30; // Example slot index for Charge Cell
        int energyCoreSlot = 22; // Example slot index for Energy Core
        int coreProcessorSlot = 14; // Example slot index for Core Processor
        int lensConduitSlot = 25; // Example slot index for Lens Conduit

        // Set up mock tiles for each core component
        inv.setItem(frameBodySlot, createComponentItem(fb.getFrameBody(), "Frame Body"));
        inv.setItem(chargeCellSlot, createComponentItem(fb.getChargeCell(), "Charge Cell"));
        inv.setItem(energyCoreSlot, createComponentItem(fb.getEnergyCore(), "Energy Core"));
        inv.setItem(coreProcessorSlot, createComponentItem(fb.getCoreProcessor(), "Core Processor"));
        inv.setItem(lensConduitSlot, createComponentItem(fb.getLensConduit(), "LensConduit"));

        // Placeholder item for empty slots
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        placeholderMeta.setDisplayName(" ");
        placeholder.setItemMeta(placeholderMeta);

        // Fill the rest of the inventory with placeholders, skipping the slots for core components
        Set<Integer> coreComponentSlots = Set.of(frameBodySlot, chargeCellSlot, energyCoreSlot, coreProcessorSlot, lensConduitSlot);
        for (int i = 0; i < inv.getSize(); i++) {
            if (!coreComponentSlots.contains(i)) {
                inv.setItem(i, placeholder);
            }
        }
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        // Logic for handling item clicks in the inventory
    }

    private ItemStack createComponentItem(CoreComponent component, String name) {
        ItemStack item = new ItemStack(Material.DIAMOND); // Choose an appropriate material
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();

        // Add information about the component to the lore
        if (component != null) {
            lore.add(component.getStats());
            // Example: lore.add("Energy: " + component.getEnergy());
            // Add other relevant information
        } else {
            lore.add(ChatColor.GRAY + "No component installed");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}