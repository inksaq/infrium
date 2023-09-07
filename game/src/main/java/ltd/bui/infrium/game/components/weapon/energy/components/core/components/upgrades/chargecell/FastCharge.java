package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell;

import lombok.Getter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class FastCharge extends ComponentUpgrade {

    @Getter
    private final double chargeRateMultiplier;

    public FastCharge(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, ComponentUpgradeType.FAST_CHARGE);
        this.chargeRateMultiplier = (tier.getRechargeRate() * rarity.getChargeRateMultiplier()) / grade.getGradeLadder() ;
    }
}
