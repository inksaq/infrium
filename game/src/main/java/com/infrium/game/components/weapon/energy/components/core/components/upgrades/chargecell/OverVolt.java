package com.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell;

import lombok.Getter;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;

public class OverVolt extends ComponentUpgrade {

    @Getter
    private final double voltageMultiplier;
    @Getter
    private final double heatRate;

    public OverVolt(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, ComponentUpgradeType.OVERVOLT);
        voltageMultiplier = rarity.getThresholdMultiplier() * tier.getLadder();
        heatRate = super.componentUpgradeType.getHeatRate();
    }
}
