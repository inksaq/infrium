package com.infrium.core.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemBuilder {

  ItemStack build(Player player);
}
