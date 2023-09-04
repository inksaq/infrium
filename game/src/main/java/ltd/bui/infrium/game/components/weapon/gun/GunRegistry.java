package ltd.bui.infrium.game.components.weapon.gun;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GunRegistry {

    private static final Map<ItemStack, AGun> guns = new HashMap<>();
    private static final Map<String, AGun> gunsByName = new HashMap<>();

    public static void registerGun(ItemStack item, AGun gun, String name) {
        guns.put(item, gun);
        gunsByName.put(name, gun);
    }

    public static AGun getGunByItem(ItemStack item) {
        return guns.get(item);
    }

    public static AGun getGunByName(String name) {
        return gunsByName.get(name);
    }

    public static boolean isGun(ItemStack item) {
        return guns.containsKey(item);
    }

    public static void giveGunToPlayer(Player player, String gunName) {
        AGun gun = getGunByName(gunName);
        if (gun instanceof InfantryWeapon) {
            ItemStack gunItem = ((InfantryWeapon) gun).getGunItem();
            player.getInventory().addItem(gunItem);
        }
    }
}
