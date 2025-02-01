package ltd.bui.infrium.game.util;

import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.ICoreComponent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ItemTickManager {
    private static final Logger logger = Logger.getLogger(ItemTickManager.class.getName());
    private final Set<ItemStack> tickableItems;

    public ItemTickManager() {
        this.tickableItems = new HashSet<>();
    }

    public void addTickableItem(ItemStack item) {
        tickableItems.add(item);
    }

    public void removeTickableItem(ItemStack item) {
        tickableItems.remove(item);
    }

    public void tickItems() {
        for (ItemStack item : tickableItems) {
            CoreComponent component = ICoreComponent.fromItemStack(item);
            if (component != null) {
                component.onTick();
                // Update the ItemStack with the ticked component
                ItemStack updatedItem = ICoreComponent.createItemStack();
                // Replace the old item with the updated one in the set
                tickableItems.remove(item);
                tickableItems.add(updatedItem);
            }
        }
        logger.fine("Ticked " + tickableItems.size() + " items");
    }

    public void clearTickableItems() {
        tickableItems.clear();
    }
}