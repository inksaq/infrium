package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.OverCharge;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public abstract class ComponentUpgrade<T extends CoreComponent>{
    @Getter @Setter
    protected Rarity rarity;
    @Getter @Setter
    protected Grade grade;
    @Getter @Setter
    protected Tier tier;

    @Getter
    private T appliedTo;

    @Getter @Setter
    protected ComponentUpgradeType componentUpgradeType;

    public ComponentUpgrade(Rarity rarity, Grade grade, Tier tier, ComponentUpgradeType componentUpgradeType) {
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
    public static ComponentUpgrade<?> deserialize(ReadWriteNBT nbt) {
        ComponentUpgradeType componentUpgradeType = ComponentUpgradeType.valueOf(nbt.getString("componentUpgradeType"));

        switch (componentUpgradeType) {
            case OVERCHARGE:
                return OverCharge.deserialize(nbt);
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
