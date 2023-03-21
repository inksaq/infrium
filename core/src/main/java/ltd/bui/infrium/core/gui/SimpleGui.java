package ltd.bui.infrium.core.gui;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleGui extends AbstractGui {

  private final List<GuiItem> guiItemList = new ArrayList<>();

  public SimpleGui(int size, Component title, JavaPlugin plugin) {
    super(size, title, plugin);
  }

  @Override
  protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
    for (var item : this.guiItemList) {
      if (item.getItemBuilder().build(player).isSimilar(clickedItem)) {
        item.getOnItemClick().onItemClick(player, clickedItem, inventory);
        break;
      }
    }
  }

  public void openInventory(Player player) {
    Inventory inv = Bukkit.createInventory(null, this.size, title);
    for (var item : this.guiItemList) {
      inv.setItem(item.getSlot(), item.getItemBuilder().build(player));
    }
    player.openInventory(inv);
  }
}
