package com.infrium.game.components.weapon.gun;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public interface AGun {
    void shoot(Player player);

    void reload();

    ItemMeta getItemMeta();
}
