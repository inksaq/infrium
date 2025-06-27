package com.infrium.game.components.weapon.energy.components.attachments;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;

public interface IFrameAttachment {
    Rarity getRarity();
    Tier getTier();
    Grade getGrade();
    FrameAttachmentType getFrameAttachmentType();

    NBTCompound serializeToNBT();
    FrameAttachment deserializeFromNBT();
}
