package ltd.bui.infrium.core.gui;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PageGui extends AbstractGui{
    public PageGui(int size, Component title, @NonNull JavaPlugin plugin) {
        super(size, title, plugin);
    }

    @Override
    public void openInventory(Player player) {

    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {

    }
}
