package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class OverCharge extends ComponentUpgrade {

    @Getter
    @Setter
    private boolean overCharge; //unlocks superclock,supervolt,superload

    @Getter @Setter
    private int heatRateMultiplier = (int) (rarity.getThresholdMultiplier());
    @Getter @Setter
    private int capacitanceMultiplier = grade.getGradeLadder() > 1 ? grade.getGradeLadder() * (int) (rarity.getCapacitanceMultiplier() * tier.getLadder()) : 0;

    public OverCharge(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, ComponentUpgradeType.OVERCHARGE);
    }

    @Override
    public ReadWriteNBT serialize() {
        ReadWriteNBT nbt = super.serialize();
        nbt.setBoolean("overCharge", overCharge);
        nbt.setInteger("heatRateMultiplier", heatRateMultiplier);
        nbt.setInteger("capacitanceMultiplier", capacitanceMultiplier);
        return nbt;
    }

    public static OverCharge deserialize(ReadWriteNBT nbt) {
        Rarity rarity = Rarity.valueOf(nbt.getString("rarity"));
        Grade grade = Grade.valueOf(nbt.getString("grade"));
        Tier tier = Tier.valueOf(nbt.getString("tier"));

        OverCharge overChargeObj = new OverCharge(rarity, grade, tier);
        overChargeObj.setOverCharge(nbt.getBoolean("overCharge"));
        overChargeObj.setHeatRateMultiplier(nbt.getInteger("heatRateMultiplier"));
        overChargeObj.setCapacitanceMultiplier(nbt.getInteger("capacitanceMultiplier"));
        return overChargeObj;
    }



}
