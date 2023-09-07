package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.ConstantCharge;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.FastCharge;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.OverCharge;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class ChargeCell extends CoreComponent {

    @Getter @Setter private double lifespan; // lifespan of chargecell(total chargeRate throughput x total outputRate + capacity) //TODO

    private FrameBody frameBodyParent;
    @Getter @Setter private double capacity; //total capacity for chargeCell(Tier based + component upgrade)
    @Getter @Setter private double currentChargeRate; // Charge rate of Cell per second,
    @Getter @Setter private double currentOutputRate; // energy output per second when able to recharge energy core
    @Getter @Setter private double heatRate; // heat output per second of idle time / increases when output rate decreases(outputRate X tier X grade
//    @Getter @Setter private Set<ComponentUpgrade> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;



    public ChargeCell(FrameBody frameBody, Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.CHARGE_CELL);
        this.frameBodyParent = frameBody;
        if (capacity == 0) capacity = tier.getCapacitance();
        if (lifespan == 0) lifespan = grade.getLifespan();
        this.upgradeLimit = rarity.getComponentUpgradeLimit();
    }

    public FrameBody getFrameBodyParent() {
        return this.frameBodyParent;
    }

    public boolean addUpgrade(ComponentUpgrade<? super CoreComponent> upgrade) {
        if (componentUpgrades.size() >= upgradeLimit)
            return false;

        this.componentUpgrades.add(upgrade);
        upgrade.setAppliedTo(this);
        return true;
    }

    public void removeUpgrade(ComponentUpgrade<? super CoreComponent> upgrade) {
        this.componentUpgrades.remove(upgrade);
        upgrade.unSetAppliedTo();
    }
    public void onTick(){
        computeAttributes();
        applyDegredation();
        chargeCore();
    }

    private void computeAttributes() {
        computeLifespan();
        computeCapacity();
        computeOutputRate();
        computeChargeRate();
        computeHeatRate();
    }

    private void applyDegredation() {
        // Reduce the capacity based on the current output rate
        capacity -= currentOutputRate;

        // Apply degradation to lifespan
        // Assuming some degradation factor (e.g., 0.01 for 1% per tick)
        double degradationFactor = 0.01;
        lifespan -= (lifespan * degradationFactor);

        // Ensure the values don't drop below zero
        if (capacity < 0) capacity = 0;
        if (lifespan < 0) lifespan = 0;
    }

    public void chargeCore() {
        System.out.println("--- Starting chargeCore ---");

        // Determine the amount of energy to transfer - this is based on the ChargeCell's output rate
        double energyToTransfer = currentOutputRate;
        System.out.println("Initial energyToTransfer: " + energyToTransfer);

        // Ensure we don't exceed the ChargeCell's current capacity
        if (energyToTransfer > capacity) {
            energyToTransfer = capacity;
            System.out.println("EnergyToTransfer capped by current capacity to: " + energyToTransfer);
        }

        // Charge the EnergyCore
        double energyCoreCurrentCapacitance = frameBodyParent.getEnergyCore().getCoreEnergyCapacitance();
        System.out.println("EnergyCore current capacitance before charge: " + energyCoreCurrentCapacitance);

        frameBodyParent.getEnergyCore().setCoreEnergyCapacitance(energyCoreCurrentCapacitance + energyToTransfer);
        System.out.println("EnergyCore capacitance after charge: " + frameBodyParent.getEnergyCore().getCoreEnergyCapacitance());

        // Reduce the ChargeCell's capacity
        capacity -= energyToTransfer;
        System.out.println("ChargeCell's remaining capacity: " + capacity);

        // Ensure values don't go negative
        if (capacity < 0) {
            capacity = 0;
            System.out.println("Corrected negative ChargeCell capacity.");
        }

        // TODO: You can add logic here to handle heat generation and any other effects as a result of charging

        System.out.println("--- Ending chargeCore ---");
    }


    private void computeLifespan() {
        lifespan *= rarity.getLifespanMultiplier();

        // Adjust for each component upgrade:
        for(ComponentUpgrade<?> upgrade : componentUpgrades) {
            // Sample: lifespan -= upgrade.getLifespanReduction();

        }
    }

    private void computeCapacity() {
        capacity *= rarity.getCapacitanceMultiplier();

        // Adjust for each component upgrade:
        for(ComponentUpgrade<?> upgrade : componentUpgrades) {
            // Sample: capacity += upgrade.getCapacityBonus();
            switch (upgrade.getComponentUpgradeType()){
                case OVERCHARGE:
                    capacity += ((OverCharge)upgrade).getCapacitanceMultiplier();
                default:
                    return;
            }
        }
    }

    private void computeOutputRate() {
        currentOutputRate = tier.getEnergyOutputRate() * rarity.getOutputRateMultiplier();
        for (ComponentUpgrade<?> upgrade : componentUpgrades) {
            switch (upgrade.getComponentUpgradeType()) {
                //voltages
                default -> {
                    return;
                }
            }
        }
    }

    private void computeHeatRate(){
//        var componentHeatRate = calculateComponentsHeatRate(componentUpgrades);
//        System.out.println(componentHeatRate + " - total comp heat Rates");
        heatRate = tier.getHeatRate();
        for (ComponentUpgrade<?> upgrade : componentUpgrades) {
            heatRate += upgrade.getTier().getHeatRate();
        }
    }

    private void computeChargeRate(){
        currentChargeRate = tier.getRechargeRate() * rarity.getChargeRateMultiplier();
        for (ComponentUpgrade<?> upgrade : componentUpgrades) {
            switch (upgrade.getComponentUpgradeType()) {
                case FAST_CHARGE -> {
                    currentChargeRate += ((FastCharge)upgrade).getChargeRateMultiplier();
                }
                case CONSTANT_CHARGE -> {
                    currentChargeRate += ((ConstantCharge)upgrade).getConstantChargeRate();
                }
                default -> {
                    return;
                }
            }
        }
    }



    // ... similar methods for OutputRate, ChargeRate, and HeatRate ...

//    public void addUpgrade(ComponentUpgrade upgrade) {
//        if (componentUpgrades.size() >= upgradeLimit) {
//            System.out.println("upgrade limit hit");
//            return;
//        }
//        componentUpgrades.add(upgrade);
//        computeAttributes();  // Recompute after adding an upgrade
//    }



    public boolean hasOverCharge() {
        return componentUpgrades.stream().anyMatch(upgrade -> upgrade.getComponentUpgradeType() == ComponentUpgradeType.OVERCHARGE);
    }

//    private double calculateComponentsHeatRate(Set<ComponentUpgrade> componentUpgrades) {
//        return componentUpgrades.stream()
//                .mapToDouble(value -> value.getTier().getHeatRate())
//                .sum();
//    }


    public boolean hasHitUpgradeLimit() {
        return componentUpgrades.size() >= upgradeLimit;
    }


}
