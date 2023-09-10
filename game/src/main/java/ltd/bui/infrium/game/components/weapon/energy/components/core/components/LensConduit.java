package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.Lense;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

import java.util.HashMap;

public class LensConduit extends CoreComponent {

    @Getter @Setter
    private HashMap<Lense, Double> componentUpgrades; // Lense, LifespanScatter Lenses, Modulated Focus (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;

    public LensConduit(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.LENS_CONDUIT);
    }
}
