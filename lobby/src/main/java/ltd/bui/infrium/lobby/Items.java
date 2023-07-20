package ltd.bui.infrium.lobby;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Items {

  private static final ItemStack compass = new ItemStack(Material.COMPASS, 1);
  private static final ItemStack chest = new ItemStack(Material.CHEST, 1);
  private static final ItemStack sword = new ItemStack(Material.NETHERITE_SWORD, 1);
  private static final ItemStack clock = new ItemStack(Material.CLOCK, 1);

  static {
    var compassItemMeta = compass.getItemMeta();
    compassItemMeta.displayName(
        LegacyComponentSerializer.legacyAmpersand().deserialize("&aQuick Selector"));
    compass.setItemMeta(compassItemMeta);

    var chestItemMeta = chest.getItemMeta();
    chestItemMeta.displayName(
        LegacyComponentSerializer.legacyAmpersand().deserialize("&eCosmetics Menu"));
    chest.setItemMeta(chestItemMeta);

    var swordItemMeta = sword.getItemMeta();
    swordItemMeta.setUnbreakable(true);
    swordItemMeta.displayName(
        LegacyComponentSerializer.legacyAmpersand().deserialize("&aPVP Enabler"));
    sword.setItemMeta(swordItemMeta);

    var clockItemMeta = clock.getItemMeta();
    clockItemMeta.displayName(
        LegacyComponentSerializer.legacyAmpersand().deserialize("&eLobby Selector"));
    clock.setItemMeta(clockItemMeta);
  }

  Items() {}

  public static void formatInventory(Player p) {
    p.getInventory().clear();
    p.setHealth(20);
    p.setFoodLevel(20);
    p.getInventory().setItem(0, compass.clone());
    p.getInventory().setItem(2, chest.clone());
    p.getInventory().setItem(6, sword.clone());
    p.getInventory().setItem(8, clock.clone());
    p.getInventory().setHeldItemSlot(0);
  }
}
