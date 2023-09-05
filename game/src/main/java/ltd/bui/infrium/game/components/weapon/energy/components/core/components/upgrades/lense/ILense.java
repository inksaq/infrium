package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense;

import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public interface ILense {
    Tier getTier();
    Rarity getRarity();
    LenseState getLenseState();
    LenseType getLenseType();
}
