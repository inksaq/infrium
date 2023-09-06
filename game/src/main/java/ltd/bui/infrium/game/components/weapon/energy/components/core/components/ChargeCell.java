package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

import java.util.Collection;
import java.util.Set;

public class ChargeCell extends CoreComponent {

    @Getter @Setter private double lifespan; // lifespan of chargecell(total chargeRate throughput x total outputRate + capacity) //TODO

    @Getter @Setter private double capacity; //total capacity for chargeCell(Tier based + component upgrade)
    @Getter @Setter private double currentChargeRate; // Charge rate of Cell per second,
    @Getter @Setter private double currentOutputRate; // energy output per second when able to recharge energy core
    @Getter @Setter private double heatRate; // heat output per second of idle time / increases when output rate decreases(outputRate X tier X grade
    @Getter @Setter private Set<ComponentUpgrade> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;

    public ChargeCell(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.CHARGE_CELL);

    }

    public void onTick(){
        calculateDegradationPerTick();
        updateComponentHeatRate();
        updateLifespan();
        updateCapacity();
        updateChargeRate();
        updateOutputRate();
    }

    public void recomputeProperties(){

    }


    public boolean hasOverCharge() {
        return componentUpgrades.stream().anyMatch(upgrade -> upgrade.getComponentUpgradeType() == ComponentUpgradeType.OVERCHARGE);
    }

    public void updateComponentHeatRate() {
        this.heatRate = this.tier.getHeatRate() + calculateComponentsHeatRate(this.componentUpgrades);
    }

    private double calculateComponentsHeatRate(Set<ComponentUpgrade> componentUpgrades) {
        return componentUpgrades.stream()
                .mapToDouble(ComponentUpgrade::getHeatRate)
                .sum();
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
    public Collection<ComponentUpgrade> getUpgrades() {
        return componentUpgrades;
    }

}
