package ltd.bui.infrium.game.components.weapon.energy.components;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ItemModifier {

    private ItemStack item;

    public ItemModifier(ItemStack item) {
        this.item = item;
    }

    // Save a UUID to the item's NBT
    public void setUUID(String key, UUID value) {
        // NBT operations
    }

    // Retrieve a UUID from the item's NBT
    public UUID getUUID(String key) {
        // NBT operations

    }

}
