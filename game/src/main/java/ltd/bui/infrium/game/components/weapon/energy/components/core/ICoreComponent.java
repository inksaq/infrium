package ltd.bui.infrium.game.components.weapon.energy.components.core;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

import java.util.function.Consumer;

public interface ICoreComponent {
    Tier getTier();
    Rarity getRarity();
    Grade getGrade();
    CoreComponentType getComponentType();

    NBTCompound serializeToNBT();

    CoreComponent deserializeFromNBT(NBTCompound nbt);
}
