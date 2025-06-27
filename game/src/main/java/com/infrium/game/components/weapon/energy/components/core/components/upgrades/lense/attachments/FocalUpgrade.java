package com.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.attachments;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import lombok.Setter;
import com.infrium.game.components.weapon.energy.components.core.CoreComponent;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;

public abstract class FocalUpgrade<T extends CoreComponent>{
    @Getter @Setter
    protected Rarity rarity;
    @Getter @Setter
    protected Grade grade;
    @Getter @Setter
    protected Tier tier;

    @Getter
    private T appliedTo;

    @Getter @Setter
    protected FocalUpgradeType componentUpgradeType;

    public FocalUpgrade(Rarity rarity, Grade grade, Tier tier, FocalUpgradeType componentUpgradeType) {
        this.rarity = rarity;
        this.grade = grade;
        this.tier = tier;
        this.componentUpgradeType = componentUpgradeType;
    }

    public ReadWriteNBT serialize() {
        ReadWriteNBT nbt = NBT.createNBTObject();
        nbt.setString("rarity", rarity.toString());
        nbt.setString("grade", grade.toString());
        nbt.setString("tier", tier.toString());
        nbt.setString("componentUpgradeType", componentUpgradeType.toString());
        return nbt;
    }

    /**
     * Deserialize a ComponentUpgrade from an NBTCompound.
     *
     * @param nbt the NBTCompound to deserialize from.
     * @return the deserialized ComponentUpgrade.
     */
    public static FocalUpgrade<?> deserialize(ReadWriteNBT nbt) {
        FocalUpgradeType componentUpgradeType = FocalUpgradeType.valueOf(nbt.getString("componentUpgradeType"));

        switch (componentUpgradeType) {
            case SCATTER:
//                return Scatter.deserialize(nbt);
            // Handle other ComponentUpgradeTypes here
            default:
                return null;
        }
    }

    public void setAppliedTo(T component) {
        this.appliedTo = component;
    }

    public void unSetAppliedTo() {
        this.appliedTo = null;
    }
}
