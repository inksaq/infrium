package com.infrium.game.components.weapon.energy.components.attachments;

public enum FrameAttachmentType {
    STEALTH(Stealth.class);


    private final Class<? extends FrameAttachment> associatedClass;

    FrameAttachmentType(Class<? extends FrameAttachment> associatedClass) {
        this.associatedClass = associatedClass;
    }

    public Class<? extends FrameAttachment> getAssociatedClass() {
        return associatedClass;
    }
}
