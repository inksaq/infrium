package ltd.bui.infrium.game.components.weapon.energy.components.core;

import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public interface ICoreComponent {
    Tier getTier();
    Rarity getRarity();
    Grade getGrade();
    CoreComponentType getComponentType();
}
