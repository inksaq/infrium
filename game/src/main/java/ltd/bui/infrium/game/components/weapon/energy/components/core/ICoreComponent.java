package ltd.bui.infrium.game.components.weapon.energy.components.core;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

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
