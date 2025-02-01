package ltd.bui.infrium.game.handlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NBTHandler {
    public static void setUniqueId(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("uniqueId", UUID.randomUUID().toString());
        item.setItemMeta(nbtItem.getItem().getItemMeta());
    }

    public static String getUniqueId(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getString("uniqueId");
    }

    public static void setCreationTime(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setLong("creationTime", System.currentTimeMillis());
        item.setItemMeta(nbtItem.getItem().getItemMeta());
    }

    public static long getCreationTime(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getLong("creationTime");
    }
}