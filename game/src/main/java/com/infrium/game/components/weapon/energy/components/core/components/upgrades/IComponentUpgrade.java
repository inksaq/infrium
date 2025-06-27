package com.infrium.game.components.weapon.energy.components.core.components.upgrades;

import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;

public interface IComponentUpgrade {
    Tier getTier();
    Rarity getRarity();
    Grade getGrade();
    ComponentUpgradeType getComponentUpgradeType();
}
