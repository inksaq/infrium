package ltd.bui.infrium.game.components.weapon.energy.components.attachments;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public interface IFrameAttachment {
    Rarity getRarity();
    Tier getTier();
    Grade getGrade();
    FrameAttachmentType getFrameAttachmentType();

    NBTCompound serializeToNBT();
    FrameAttachment deserializeFromNBT();
}
