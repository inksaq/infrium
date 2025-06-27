package com.infrium.game.components.weapon.energy.components;

import de.tr7zw.changeme.nbtapi.iface.NBTHandler;
import com.infrium.game.components.weapon.energy.components.core.components.FrameBody;

public abstract class FrameBodyNBTHandler implements NBTHandler<FrameBody> {

//    @Override
//    public void set(@NotNull ReadWriteNBT nbt, @NotNull String key, @NotNull FrameBody value) {
//        nbt.removeKey(key); // Removing any existing key
//        nbt.getOrCreateCompound(key).mergeCompound(value.serializeToNBT()); // Storing serialized data
//    }
//
//    @Override
//    public FrameBody get(@NotNull ReadWriteNBT nbt, @NotNull String key) {
//        if (nbt.get(key, FrameBody.class)) { // Check if the key exists
//            ReadableNBT frameBodyNBT = nbt.getCompound(key); // Getting the compound associated with the key
//            FrameBody fb = new FrameBody();
//            fb.deserializeFromNBT(frameBodyNBT); // Assuming FrameBody has a method to populate itself from NBT
//            return fb;
//        }
//        return null; // Or return a new default FrameBody, based on your requirements
//    }
//
//    public static FrameBody deserialize(ReadWriteNBT nbt) {
//        var grade = nbt.get(key("grade"), INTEGER);
//        var rarity = nbt.get(key("rarity"), INTEGER);
//        var tier = nbt.get(key("tier"), INTEGER);
//        var energyCore = nbt.get(key("energyCore"), WeaponComponent.getInstance().getEnergyCoreDataType());
//        var coreProcessor = nbt.get(key("coreProcessor"), WeaponComponent.getInstance().getEnergyCoreDataType());
//        var chargeCell = nbt.get(key("chargeCell"), WeaponComponent.getInstance().getEnergyCoreDataType());
//        var lensConduit = nbt.get(key("lensConduit"), WeaponComponent.getInstance().getEnergyCoreDataType());
//        var maxFrameAttachments = nbt.get(key("maxFrameAttachments"), INTEGER);
//        return new FrameBody(grade, rarity, tier, energyCore, coreProcessor, chargeCell, lensConduit, maxFrameAttachments);
//    }
}
