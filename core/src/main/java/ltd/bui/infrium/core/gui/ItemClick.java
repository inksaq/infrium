package ltd.bui.infrium.core.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemClick {

  void onItemClick(Player player, ItemStack item, Inventory inventory);
}
