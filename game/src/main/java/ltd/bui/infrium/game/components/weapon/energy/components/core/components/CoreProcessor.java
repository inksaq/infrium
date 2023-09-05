package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

import java.util.Set;

public class CoreProcessor extends CoreComponent {
    @Getter @Setter private double lifespan; // lifespan of chargecell(lifespan - ghz*componentCount) //TODO
    @Getter @Setter private double gigaHertz; // Processor bandwidth for componenets (component tier ghz + overclock multiplier) //TODO
    @Getter @Setter private double idleDraw; // amount of idle draw of from chargecell per tick ,
    @Getter @Setter private double maxFrameAttachments; // tier based,
    @Getter @Setter private double heatRate; // heat output per second of idle time / increases when output rate decreases(outputRate X tier X grade

    @Getter @Setter private Set<ComponentUpgrade> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;

    public CoreProcessor(Rarity rarity, Grade grade, Tier tier, CoreComponentType componentType,
                         double lifespan, double gigaHertz, double idleDraw, double outputRate, double heatRate, Set<ComponentUpgrade> componentUpgrades, Integer upgradeLimit) {
        super(rarity, grade, tier, componentType);
        this.lifespan = lifespan;
        this.gigaHertz = gigaHertz;
        this.idleDraw = idleDraw;
        this.outputRate = outputRate;
        this.heatRate = heatRate;
        this.componentUpgrades = componentUpgrades;
        this.upgradeLimit = upgradeLimit;
    }

    public boolean hasHitUpgradeLimit() {
        return componentUpgrades.size() >= upgradeLimit;
    }

    public boolean addUpgrade(ComponentUpgrade componentUpgrade) {
        if (componentUpgrades.size() >= upgradeLimit) {
            System.out.println("upgrade limit hit");
            return false;
        }
        componentUpgrades.add(componentUpgrade);
        return true;
    }

    public boolean removeUpgrade(ComponentUpgrade componentUpgrade){
        if (componentUpgrades.size() == 0) {
            System.out.println("you have no more upgrades to remove");
            return false;
        }
        componentUpgrades.remove(componentUpgrade);
        return true;
    }
}
