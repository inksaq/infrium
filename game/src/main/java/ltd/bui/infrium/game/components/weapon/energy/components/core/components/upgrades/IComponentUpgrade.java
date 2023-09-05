package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades;

import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public interface IComponentUpgrade {
    Tier getTier();
    Rarity getRarity();
    Grade getGrade();
    ComponentUpgradeType getComponentUpgradeType();
}
