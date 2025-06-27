package com.infrium.game.components.weapon.gun;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class InfantryWeapon implements AGun {
    @Getter
    private final ItemStack gunItem;
    private final double damage;
    private final int magazineSize;
    private int bulletsLeft;
    private int totalBullets; // for demonstration; adjust as needed

    public InfantryWeapon(ItemStack gunItem, double damage, int magazineSize, int initialTotalBullets) {
        this.gunItem = gunItem;
        this.damage = damage;
        this.magazineSize = magazineSize;
        this.bulletsLeft = magazineSize; // assume full magazine on instantiation
        this.totalBullets = initialTotalBullets;
    }

    @Override
    public ItemMeta getItemMeta() {
        return null;
    }

    public int getBulletsLeft() {
        return bulletsLeft;
    }

    public int getTotalBullets() {
        return totalBullets;
    }

    public void shoot(Player player) {
        if (bulletsLeft > 0) {
            bulletsLeft--;
            // rest of the shooting mechanics
        } else {
            // gun is empty, might play a click sound or prompt reload
        }
    }

    public void reload() {
        if (totalBullets <= 0) {
            // no bullets left to reload
            return;
        }

        int bulletsNeeded = magazineSize - bulletsLeft;
        if (totalBullets >= bulletsNeeded) {
            bulletsLeft = magazineSize;
            totalBullets -= bulletsNeeded;
        } else {
            bulletsLeft += totalBullets;
            totalBullets = 0;
        }
    }

    // Other methods and properties...
}

