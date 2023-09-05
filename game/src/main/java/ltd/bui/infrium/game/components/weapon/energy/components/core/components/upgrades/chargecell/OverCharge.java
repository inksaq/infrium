package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class OverCharge extends ComponentUpgrade {

    @Getter
    @Setter
    private boolean overCharge; //unlocks superclock,supervolt,superload

    public OverCharge(Rarity rarity, Grade grade, Tier tier, ComponentUpgradeType componentUpgradeType) {
        super(rarity, grade, tier, componentUpgradeType);
    }






}
