package ltd.bui.infrium.game.components.weapon.energy.components.gui;

import lombok.NonNull;
import ltd.bui.infrium.core.gui.AbstractGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FrameBodyGui extends AbstractGui {


    public FrameBodyGui(int size, Component title, @NonNull JavaPlugin plugin) {
        super(size, title, plugin);
    }

    @Override
    public void openInventory(Player player) {

    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {

    }
}
