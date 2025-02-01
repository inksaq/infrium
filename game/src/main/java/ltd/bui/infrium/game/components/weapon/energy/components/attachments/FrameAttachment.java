package ltd.bui.infrium.game.components.weapon.energy.components.attachments;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public abstract class FrameAttachment implements IFrameAttachment{
    @Getter @Setter
    protected Rarity rarity;
    @Getter @Setter
    protected Grade grade;
    @Getter @Setter
    protected Tier tier;
    @Getter @Setter
    protected FrameAttachmentType frameAttachmentType;


    public FrameAttachment(Rarity rarity, Grade grade, Tier tier, FrameAttachmentType frameAttachmentType) {
        this.rarity = rarity;
        this.grade = grade;
        this.tier = tier;
        this.frameAttachmentType = frameAttachmentType;
    }
}
