package ltd.bui.infrium.game.components.weapon.energy.components.attachments;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class Stealth extends FrameAttachment {

    @Getter @Setter
    private boolean stealthMode;

    public Stealth(Rarity rarity, Grade grade, Tier tier, FrameAttachmentType frameAttachmentType, boolean stealthMode) {
        super(rarity, grade, tier, frameAttachmentType);
        this.stealthMode = stealthMode;
    }

    @Override
    public NBTCompound serializeToNBT() {
        return null;
    }

    @Override
    public FrameAttachment deserializeFromNBT() {
        return null;
    }
}
