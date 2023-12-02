package ltd.bui.infrium.game.components.weapon.energy.components;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.NBTHandler;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import ltd.bui.infrium.game.components.weapon.energy.components.attachments.FrameAttachment;
import org.jetbrains.annotations.NotNull;


public class FrameBodyNBTHandler {
//    @Override
//    public boolean fuzzyMatch(Object obj) {
//        return NBTHandler.super.fuzzyMatch(obj);
//    }
//
//    @Override
//    public void set(@Nonnull ReadWriteNBT nbt, @Nonnull String key, @Nonnull FrameBody weapon) {
//        NBTCompound weaponCompound = new NBTCompound();
//
//        // Saving the fields to the compound
//        weaponCompound.setUUID("frameUUID", weapon.getFrameUUID());
//
//        // ... Save other singular values (like int, Grade, etc.) ...
//        weaponCompound.setInteger("lifespan", weapon.getLifespan());
//
//        // Assuming you have NBTHandlers for complex objects like FrameBody, EnergyCore, etc.
//        weaponCompound.setC("frameBody", new FrameBodyNBTHandler().get(nbt, "frameBody"));
//        // ... Do the same for other complex objects ...
//
//        // For sets, you'll need to handle each element.
//        NBTList<NBTCompound> frameAttachmentsList = new NBTList<>(NBTType.NBTTagCompound);
//        for (FrameAttachment attachment : weapon.getFrameAttachments()) {
//            // Assuming FrameAttachment has an NBT serialization method
//            frameAttachmentsList.add(attachment.toNBT());
//        }
//        weaponCompound.setList("frameAttachments", frameAttachmentsList);
//
//        // Store the weapon compound into the main NBT
//        nbt.setCompound(key, weaponCompound);
//    }
//
//    @Override
//    public FrameBody get(@Nonnull ReadableNBT nbt, @Nonnull String key) {
//        if (!nbt.hasKey(key)) {
//            return null;
//        }
//
//        NBTCompound weaponCompound = nbt.getCompound(key);
//        Weapon weapon = new Weapon();
//
//        // Retrieve the fields from the compound
//        weapon.setFrameUUID(weaponCompound.getUUID("frameUUID"));
//
//        // ... Load other singular values ...
//
//        // For complex objects, you'll use their respective handlers.
//        weapon.setFrameBody(new FrameBodyNBTHandler().get(weaponCompound, "frameBody"));
//        // ... Do the same for other complex objects ...
//
//        // For sets, handle each element.
//        Set<FrameAttachment> frameAttachments = new HashSet<>();
//        NBTList<NBTCompound> frameAttachmentsList = weaponCompound.getList("frameAttachments", NBTType.NBTTagCompound);
//        for (NBTCompound attachmentCompound : frameAttachmentsList) {
//            FrameAttachment attachment = FrameAttachment.fromNBT(attachmentCompound);
//            frameAttachments.add(attachment);
//        }
//        weapon.setFrameAttachments(frameAttachments);
//
//        return weapon;
//    }
}
