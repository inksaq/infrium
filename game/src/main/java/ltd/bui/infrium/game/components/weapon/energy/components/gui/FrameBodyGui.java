package ltd.bui.infrium.game.components.weapon.energy.components.gui;

import lombok.NonNull;
import ltd.bui.infrium.core.gui.AbstractGui;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FrameBodyGui extends AbstractGui {

    private FrameBody frameBody;

    public FrameBodyGui(FrameBody frameBody, JavaPlugin plugin) {
        super(27, Component.text("Frame Body"), plugin);
        this.frameBody = frameBody;
    }

    @Override
    public void openInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "WEP");

        // Populate with current components or placeholders
        inv.setItem(0, frameBody.getChargeCell() != null ? new ItemStack(Material.INK_SAC) : new ItemStack(Material.AIR));
        inv.setItem(1, frameBody.getEnergyCore() != null ? new ItemStack(Material.DIAMOND) : new ItemStack(Material.AIR));
        // ... and so on for other components

        player.openInventory(inv);
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        // Check if clicked item is a placeholder
        // If so, check player's inventory for corresponding component and add if found
        // Otherwise, open the component-specific GUI
    }
}
