package com.infrium.game.components.weapon.energy.components.core.components.upgrades.lense;

import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;

public interface ILense {
    Tier getTier();
    Rarity getRarity();
    LenseState getLenseState();
    LenseType getLenseType();
}
