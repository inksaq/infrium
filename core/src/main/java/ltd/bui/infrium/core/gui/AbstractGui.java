package ltd.bui.infrium.core.gui;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractGui implements Listener {

    protected final int size;
    protected Component title;

    public AbstractGui(int size, Component title, @NonNull JavaPlugin plugin) {
        this.size = size;
        this.title = title;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(ignoreCancelled = true)
    public final void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().title().equals(this.title)) return;

        event.setCancelled(true);

        final ItemStack clickedItem = event.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        onItemClick(player, clickedItem, event.getView().getTopInventory());
    }

    @EventHandler
    public final void inventoryDragEvent(final InventoryDragEvent e) {
        if (e.getView().title().equals(this.title)) {
            e.setCancelled(true);
        }
    }

    public abstract void openInventory(Player player);

    protected abstract void onItemClick(Player player, ItemStack clickedItem, Inventory inventory);

    public void fill(Inventory inv, ItemStack item, int start, int end) {
        for (int i = start; i < end; i++) {
            inv.setItem(i, item);
        }
    }


}
