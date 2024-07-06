package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.Getter;
import lombok.Setter;
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
import org.bukkit.ChatColor;

import java.util.*;

@Setter
@Getter
public class ChargeCell extends CoreComponent {

    private ChargeCell chargeCell;

    private FrameBody frameBodyParent;
    private UUID uuid;
    private double lifespan; // lifespan of chargecell(total chargeRate throughput x total outputRate + capacity) //TODO

    private int capacity; //total capacity for chargeCell(Tier based + component upgrade)
    private int currentChargeRate; // Charge rate of Cell per second,
    private int currentOutputRate; // energy output per second when able to recharge energy core
    private int heatRate; // heat output per second of idle time / increases when output rate decreases(outputRate X tier X grade
    private Set<ComponentUpgrade<?>> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
    private Integer upgradeLimit;


    public List<String> getChargeCellLore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Charge Cell");
        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GRAY + "UUID: " + ChatColor.WHITE + chargeCell.getUuid());
        lore.add(ChatColor.GRAY + "Tier: " + ChatColor.WHITE + chargeCell.getTier());
        lore.add(ChatColor.GRAY + "Grade: " + ChatColor.WHITE + chargeCell.getGrade());
        lore.add(ChatColor.GRAY + "Rarity: " + ChatColor.WHITE + chargeCell.getRarity());
        lore.add(ChatColor.GRAY + "Capacity: " + ChatColor.WHITE + chargeCell.getCapacity() + " units");
        lore.add(ChatColor.GRAY + "Recharge Rate: " + ChatColor.WHITE + chargeCell.getCurrentChargeRate() + " units/s");
        lore.add(ChatColor.GRAY + "Output Rate: " + ChatColor.WHITE + chargeCell.getCurrentOutputRate() + " units/s");
        lore.add(ChatColor.GRAY + "Heat Rate: " + ChatColor.WHITE + chargeCell.getHeatRate() + " °C/s");
        lore.add(ChatColor.GRAY + "Lifespan: " + ChatColor.WHITE + chargeCell.getLifespan() + " s");
        lore.add(ChatColor.GRAY + "Upgrades: " + (chargeCell.getComponentUpgrades() != null ? "[" + chargeCell.getComponentUpgrades().size() + "/" + chargeCell.getUpgradeLimit() + "] (click for upgrades)" : "[0/0] (click for upgrades)"));
        if (chargeCell.getComponentUpgrades() != null) {
            chargeCell.getComponentUpgrades().forEach(upgrade -> lore.add(ChatColor.GRAY + "  - " + upgrade.getComponentUpgradeType().name()));
        }
        lore.add(ChatColor.GRAY + "---------------------");

        return lore;
    }


    public ChargeCell(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.CHARGE_CELL);
        chargeCell = this;
        this.uuid = UUID.randomUUID();
        if (capacity == 0) capacity = tier.getCapacitance();
        if (lifespan == 0) lifespan = grade.getLifespan();
        this.upgradeLimit = (Integer) rarity.getComponentUpgradeLimit();
        this.currentChargeRate = tier.getRechargeRate();
        this.currentOutputRate = tier.getEnergyOutputRate();
        this.heatRate = tier.getHeatRate();
    }

    @Override
    public NBTCompound serializeToNBT() {
        return null;
    }

    @Override
    public CoreComponent deserializeFromNBT(NBTCompound nbt) {
        return null;
    }


    public boolean addUpgrade(ComponentUpgrade<? super CoreComponent> upgrade) {
        if (componentUpgrades == null) componentUpgrades = new HashSet<>(upgradeLimit);

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
        if (getFrameBodyParent().getEnergyCore() != null) {
            chargeCore();
            System.out.println("Core Charged");
        }
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
        capacity = currentOutputRate - capacity;

        // Apply degradation to lifespan
        // Assuming some degradation factor (e.g., 0.01 for 1% per tick)

        double degradationFactor = frameBodyParent.getCoreProcessor() != null ? frameBodyParent.getCoreProcessor().getGigaHertz() * tier.getLadder() : 1;
        lifespan -= (lifespan * degradationFactor);

        // Ensure the values don't drop below zero
        if (capacity < 0) capacity = 0;
        if (lifespan < 0) lifespan = 0;
    }

    public void chargeCore() {
        System.out.println("--- Starting chargeCore ---");

        // Determine the amount of energy to transfer - this is based on the ChargeCell's output rate
        int energyToTransfer = currentOutputRate;
        System.out.println("Initial energyToTransfer: " + energyToTransfer);

        // Ensure we don't exceed the ChargeCell's current capacity

        if (frameBodyParent.getEnergyCore() != null) {

            if (energyToTransfer > capacity) {
                energyToTransfer = capacity;
                System.out.println("EnergyToTransfer capped by current capacity to: " + energyToTransfer);
            }
            // Charge the EnergyCore
            int energyCoreCurrentCapacitance = frameBodyParent.getEnergyCore().getCoreEnergyCapacitance();
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
        } else {
            System.out.println("No EnergyCore Equiped.");
        }

        // TODO: You can add logic here to handle heat generation and any other effects as a result of charging

        System.out.println("--- Ending chargeCore ---");
    }


    private void computeLifespan() {
        lifespan *= rarity.getLifespanMultiplier();

        // Adjust for each component upgrade:
        if (componentUpgrades != null){
        for(ComponentUpgrade<?> upgrade : componentUpgrades) {
            // Sample: lifespan -= upgrade.getLifespanReduction();

        }
        }
    }

    private void computeCapacity() {
        capacity *= rarity.getCapacitanceMultiplier();

        // Adjust for each component upgrade:
if (componentUpgrades == null) return;
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
        currentOutputRate = (int) (tier.getEnergyOutputRate() * rarity.getOutputRateMultiplier());
        if (componentUpgrades == null) return;

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
        if (componentUpgrades == null) return;

        for (ComponentUpgrade<?> upgrade : componentUpgrades) {
            heatRate += upgrade.getTier().getHeatRate();
        }
    }

    private void computeChargeRate(){
        currentChargeRate = (int) (tier.getRechargeRate() * rarity.getChargeRateMultiplier());
        if (componentUpgrades == null) return;

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
