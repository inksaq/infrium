package com.infrium.game.components.testing.ui;

import com.infrium.core.gui.AbstractGui;
import com.infrium.game.components.weapon.energy.components.core.components.*;
import com.infrium.game.components.weapon.energy.components.core.CoreComponent;
import com.infrium.game.components.weapon.registry.WeaponRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class WorkbenchGUI extends AbstractGui {
    private FrameBody fb;

    private final int frameBodySlot = 19;  // Example slot index for Frame Body
    private final int chargeCellSlot = 30; // Example slot index for Charge Cell
    private final int energyCoreSlot = 22; // Example slot index for Energy Core
    private final int coreProcessorSlot = 14; // Example slot index for Core Processor
    private final int lensConduitSlot = 25; // Example slot index for Lens Conduit

    public WorkbenchGUI(Component title, JavaPlugin plugin) {
        super(45, title, plugin); // Assuming a 5x9 inventory for the workbench
    }

    @Override
    public void openInventory(Player player) {
    }

    public void openInventory(Player player, ItemStack droppedItem) {
        fb = WeaponRegistry.getInstance().getFrameBody(droppedItem);
        Inventory inv = Bukkit.createInventory(player, size, title);

        // Initialize the inventory with slots for weapon, components, and upgrades
//        initializeInventory(inv);

        player.openInventory(inv);
    }

//    private void initializeInventory(Inventory inv) {
//        // Assuming FrameBody `fb` has already been set
//        if (fb == null) return;
//
//        // Set up mock tiles for each core component
//        inv.setItem(frameBodySlot, createComponentItem(fb, "Frame Body"));
//        inv.setItem(chargeCellSlot, createComponentItem(fb.getChargeCell(), "Charge Cell"));
//        inv.setItem(energyCoreSlot, createComponentItem(fb.getEnergyCore(), "Energy Core"));
//        inv.setItem(coreProcessorSlot, createComponentItem(fb.getCoreProcessor(), "Core Processor"));
//        inv.setItem(lensConduitSlot, createComponentItem(fb.getLensConduit(), "Lens Conduit"));
//
//        // Placeholder item for empty slots
//        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
//        ItemMeta placeholderMeta = placeholder.getItemMeta();
//        placeholderMeta.setDisplayName("");
//        placeholder.setItemMeta(placeholderMeta);
//
//        // Fill the rest of the inventory with placeholders, skipping the slots for core components
//        Set<Integer> coreComponentSlots = Set.of(frameBodySlot, chargeCellSlot, energyCoreSlot, coreProcessorSlot, lensConduitSlot);
//        for (int i = 0; i < inv.getSize(); i++) {
//            if (!coreComponentSlots.contains(i)) {
//                inv.setItem(i, placeholder);
//            }
//        }
//    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        if (clickedItem == null || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return; // Ignore clicks on placeholder items
        }

        int slot = inventory.first(clickedItem);
        CoreComponent removedComponent = null;

        // Check which slot was clicked and handle the removal
        if (slot == frameBodySlot) {
            // Frame Body cannot be removed in this context
            return;
//        } else if (slot == chargeCellSlot) {
//            removedComponent = fb.getChargeCell();
//            fb.setChargeCell(null);
//        } else if (slot == energyCoreSlot) {
//            removedComponent = fb.getEnergyCore();
//            fb.setEnergyCore(null);
//        } else if (slot == coreProcessorSlot) {
//            removedComponent = fb.getCoreProcessor();
//            fb.setCoreProcessor(null);
//        } else if (slot == lensConduitSlot) {
//            removedComponent = fb.getLensConduit();
//            fb.setLensConduit(null);
//        }



            // Update the inventory
//        initializeInventory(inventory);
        }
    }
}

//    private ItemStack createComponentItem(CoreComponent component, String name) {
//        if (component == null) {
//            return new ItemStack(Material.AIR);
//        }
//
//        ItemStack item = new ItemStack(Material.DIAMOND); // Choose an appropriate material
//        ItemMeta meta = item.getItemMeta();
//        meta.setDisplayName(component.getRarity().getRarityFormat() +  " " + component.getGrade().getGradeFormat() + " " + component.getTier().getTierFormat() + " | " + name.toUpperCase());
//        List<String> lore = new ArrayList<>();
//
//        // Add information about the component to the lore
//        if (component instanceof FrameBody frameBody) {
////            lore = frameBody.getFrameBodyLore();
//        } else if (component instanceof ChargeCell chargeCell) {
//            lore = chargeCell.getChargeCellLore();
//        } else if (component instanceof EnergyCore energyCore) {
//            lore = energyCore.getEnergyCoreLore();
//        } else if (component instanceof LensConduit lensConduit) {
//            lore = lensConduit.getLensConduitLore();
//        } else if (component instanceof CoreProcessor coreProcessor) {
//            lore = coreProcessor.getCoreProcessorLore();
//        } else if (component.getComponentType() == CoreComponentType.EMPTY) {
//            lore.add(ChatColor.GRAY + "No component installed");
//        }
//
//        meta.setLore(lore);
//        item.setItemMeta(meta);
//        return item;
//    }

