package com.infrium.game.components.weapon.registry;

import com.infrium.game.components.weapon.workstation.DevChest;
import com.infrium.game.components.weapon.workstation.weapon.WeaponWorkstation;
import com.infrium.game.util.ItemTickManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class WeaponDevelopmentSystem implements Listener {
    private static final Logger logger = Logger.getLogger(WeaponDevelopmentSystem.class.getName());
    private final Plugin plugin;
    private final ItemTickManager itemTickManager;
    private final WeaponWorkstation weaponWorkstation;
    private final DevChest devChest;

    public WeaponDevelopmentSystem(Plugin plugin, WeaponWorkstation weaponWorkstation) {
        this.plugin = plugin;
        this.itemTickManager = new ItemTickManager();
        this.weaponWorkstation = weaponWorkstation;
        this.devChest = new DevChest(plugin);
        logger.info("WeaponDevelopmentSystem initialized");
    }

    public void openWorkstation(Player player) {
        weaponWorkstation.openWorkstation(player);
    }

    public void addItemToDevChest(Player player, ItemStack item) {
        devChest.addItem(player, item);
        itemTickManager.addTickableItem(item);
    }

    public void removeItemFromDevChest(Player player, int index) {
        ItemStack removedItem = devChest.removeItem(player, index);
        if (removedItem != null) {
            itemTickManager.removeTickableItem(removedItem);
        }
    }

    public void openDevChest(Player player) {
        devChest.openDevChest(player);
    }

    public void tickItems() {
        itemTickManager.tickItems();
    }

    // Other methods for managing the workstation and dev chest
}
