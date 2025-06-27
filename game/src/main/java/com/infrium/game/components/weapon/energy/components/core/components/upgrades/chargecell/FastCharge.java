package com.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell;

import lombok.Getter;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;

public class FastCharge extends ComponentUpgrade {

    @Getter
    private final double chargeRateMultiplier;

    public FastCharge(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, ComponentUpgradeType.FAST_CHARGE);
        this.chargeRateMultiplier = (tier.getRechargeRate() * rarity.getChargeRateMultiplier()) / grade.getGradeLadder() ;
    }
}
