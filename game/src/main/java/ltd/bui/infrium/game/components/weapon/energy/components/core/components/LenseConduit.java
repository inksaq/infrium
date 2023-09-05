package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.Lense;

import java.util.HashMap;

public class LenseConduit extends CoreComponent {

    @Getter @Setter
    private HashMap<Lense, Double> componentUpgrades; // Lense, LifespanScatter Lenses, Modulated Focus (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;





}
