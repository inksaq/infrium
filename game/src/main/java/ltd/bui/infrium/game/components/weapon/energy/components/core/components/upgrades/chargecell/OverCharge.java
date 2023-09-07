package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell;

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
    private double heatRateMultiplier = rarity.getThresholdMultiplier();
    @Getter
    private double capacitanceMultiplier = grade.getGradeLadder() > 1 ? grade.getGradeLadder() * rarity.getCapacitanceMultiplier() * tier.getLadder() : 0.00;

    public OverCharge(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, ComponentUpgradeType.OVERCHARGE);
    }



}
