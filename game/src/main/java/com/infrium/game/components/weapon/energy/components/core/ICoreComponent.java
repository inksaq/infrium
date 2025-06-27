package com.infrium.game.components.weapon.energy.components.core;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;
import org.bukkit.inventory.ItemStack;

public interface ICoreComponent {
    Tier getTier();
    Rarity getRarity();
    Grade getGrade();
    CoreComponentType getComponentType();
    void addUpgrade(ComponentUpgrade<? extends CoreComponent> upgrade);
//
    void onTick();
//    void computeAttributes();

    static CoreComponent fromItemStack(ItemStack itemStack) { return null;}

    static ItemStack createItemStack() {return null;}
//
    NBTCompound serializeToNBT();



    //
    static CoreComponent deserializeFromNBT(NBTCompound nbt) {
        return null;
    }
}
