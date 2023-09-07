package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.ConstantCharge;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.OverVolt;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

import java.util.Set;

public class EnergyCore extends CoreComponent {

    private FrameBody frameBodyParent;

    @Getter @Setter private double lifespan; /*Base lifespan is 2 years(in seconds),
     each added component to the Body reduces the lifespan on each component with lifespan(processor and chargecell(with different lifespans)
      but same calculation effecting all)
      lifespan is effected by Tier, Grade and Rarity*/
    @Getter @Setter private double rechargeDelay; // charging delay between max sustain and being able to output energy, cooldown(extra delay) may effect recharge rate and also upgrades, quickcharge, constantcharge may affect it too.
    @Getter @Setter private double rechargeRate; // Calculates rate at which energycore charges, affected by stability rating and heatRate and cooldown(cooldown in cooldown may not fire and half recharge rate with addition of extra recharge delay)
    @Getter @Setter private double stabilityRating; // percentage value 0 - 100.00% indicating processor stability with upgradecomponents
    @Getter @Setter private double stabilityDropRate; // calculated by taking into account, voltage rating, energyoutput rate, maxsustaintime, heatrate to generate a value when sustainTime increases to also decrease stability reducing it from stabilityRating overtime, cooldowns affect this too
    @Getter @Setter private double voltageRating; //Default is 1.05 affected by overclocking (calculated by taking into account(tier, rarity, grade) and multiplying by voltage rating(voltageRating can be affected by adding overvolt or supervolt and going under with undervolt)
    @Getter @Setter private double voltageThreshold; // default is (Tier*(Rarity/2) * Grade * voltage rating) * voltageRating
    @Getter @Setter private double outputEnergyRate; // output rate is calculated by having a base output rate calculated from(tier,grade,rarity) and them modifiers are added by the likes of overvolt,
    @Getter @Setter private double coreEnergyCapacitance; // capacitance is a set value(altered by overload component) and is where energy is taken when fired, no matter max sustain time, if output energyrate depletes capacitance before sustain time is reached, it will weaken output(only output recharge delay(until max sustain is hit))
    @Getter @Setter private double outputEnergyRateThreshold; // threshold is calculated by overload(requires overvolt of chargecell) and underload(no min requirement) core component upgrades
    @Getter @Setter private double sustainTime; // sustain time is counted when user holds down click, it draws energy from coreEnergyCapactiance and maintained by outputEnergyRate
    @Getter @Setter private double maxSustainRate; // calculated by added core component upgrades added(overload(requires chargecell to have overvolt),superload(requires overcharge,superclock and supervolt to achieve superload((, No requirement for underload))
    @Getter @Setter private double idleDrawRate; // calculates corecomponents and upgrades added, and takes charge from chargecell capacitance
    @Getter @Setter private double HeatRate; // calculates idle heat, all corecomponents and upgrades create a baseline, then when energyoutput happens, calculated by getting voltage rating and distance to voltage threshold, sustain and distance to max sustain(The need to cooldown)
    @Getter @Setter private double heatStabilityThreshold; // Heat Threshold before stability drop rate increases affecting stabilitiy rating, modified by framebody heat exchanging attachments

    @Getter @Setter private Set<ComponentUpgrade> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;

    public EnergyCore(FrameBody frameBody,Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.ENERGY_CORE);
        this.frameBodyParent = frameBody;
        this.lifespan = grade.getLifespan();
        this.coreEnergyCapacitance = 0;
        this.upgradeLimit = 2;
        this.sustainTime = 0;
        this.stabilityRating = 100;
        this.rechargeDelay = 2; //todo
        this.idleDrawRate = tier.getIdleDraw() * (1
                        + /*getFrameBodyParent().getCoreProcessor().getComponentUpgrades().stream().mapToDouble(upgrade -> upgrade.getTier().getIdleDraw()).sum()*/
                        (getFrameBodyParent().getChargeCell() == null ? 0 : getFrameBodyParent().getChargeCell().getUpgrades().stream().mapToDouble(upgrade -> upgrade.getTier().getIdleDraw()).sum())
        );
    }

    public FrameBody getFrameBodyParent() {
        return this.frameBodyParent;
    }

    public void onTick(){
        computeAttributes();
        applyDegredation();
    }

    private void applyDegredation() {
        // Variables
        double lifespanDegradationFactor = 0.01; // 1% per tick
        double sustainedOutputDegradation = sustainTime * outputEnergyRate / coreEnergyCapacitance;

        // Degradation from output energy rate and sustained output
        lifespan -= sustainedOutputDegradation;

        // Degradation from idle draw rate
        lifespan -= idleDrawRate;

        // Apply a generic degradation factor to lifespan
        lifespan -= (lifespan * lifespanDegradationFactor);

        // Ensure values don't go negative
        if (outputEnergyRate < 0) outputEnergyRate = 0;
        if (lifespan < 0) lifespan = 0;
        if (sustainTime < 0) sustainTime = 0;
    }


    private void computeAttributes() {
        computeLifespan();
        computeRechargeDelay();
        computeRechargeRate();
        computeStabilityRating();
        computeStabilityDropRate();
        computeVoltageRating();
        computeVoltageThreshold();
        computeOutputEnergyRate();
        computeCoreEnergyCapacitance();
        computeOutputEnergyRateThreshold();
        computeSustainTime();
        computeMaxSustainRate();
        computeIdleDrawRate();
        computeHeatRate();
        computeHeatStabilityThreshold();
    }

    public void expendEnergy() {
        System.out.println("--- Starting expendEnergy ---");

        // Energy expenditure
        double energyUsed = outputEnergyRate;
        if(coreEnergyCapacitance < energyUsed) {
            energyUsed = coreEnergyCapacitance;
        }
        coreEnergyCapacitance -= energyUsed;
        System.out.println("Energy Used: " + energyUsed);
        System.out.println("Remaining coreEnergyCapacitance: " + coreEnergyCapacitance);

        // Sustain time update
        sustainTime += 1;  // Assuming this is called once per tick/unit-time
        if(sustainTime > maxSustainRate) {
            sustainTime = maxSustainRate;  // Ensure it doesn't exceed max
        }
        System.out.println("Updated sustainTime: " + sustainTime);

        // Heat generation
        double voltageDifference = voltageRating - voltageThreshold;
        double sustainDifference = sustainTime - maxSustainRate;
        HeatRate += (voltageDifference + sustainDifference) / 2;
        System.out.println("HeatRate after energy use: " + HeatRate);

        // Stability management
        double heatImpact = (HeatRate > heatStabilityThreshold) ? 2 : 1; // e.g., doubles the drop rate when we cross the threshold
        stabilityRating -= stabilityDropRate * heatImpact;
        System.out.println("Updated stabilityRating: " + stabilityRating);

        // Respect for the max sustained output
        if(sustainTime == maxSustainRate) {
            outputEnergyRate *= 0.5;  // e.g., Halve the output energy rate when sustain time is maxed out.
            System.out.println("Sustain time maxed out. New outputEnergyRate: " + outputEnergyRate);
        }

        // Ensure values don't go below their minimum limits
        if(coreEnergyCapacitance < 0) coreEnergyCapacitance = 0;
        if(stabilityRating < 0) stabilityRating = 0;
        if(HeatRate < 0) HeatRate = 0;

        System.out.println("--- Ending expendEnergy ---");
    }



    private void computeLifespan() {
        lifespan -= componentUpgrades != null ? componentUpgrades.size(): 0 * rarity.getThresholdMultiplier(); // You may want a factor to degrade lifespan
    }

    private void computeRechargeDelay() {
        // sample calculation
        rechargeDelay = (getRechargeDelay() / 100) + (getHeatRate() / 10);
        // Loop through componentUpgrades to modify rechargeDelay based on their effects
    }

    private void computeRechargeRate() {
        // sample calculation
        rechargeRate = getRechargeRate() * (stabilityRating / 100) - (getHeatRate() / 10);
        // Loop through componentUpgrades to modify rechargeRate based on their effects
    }

    private void computeStabilityRating() {
        // basic idea
        double aggregateHeatRate =
                /*getFrameBodyParent().getCoreProcessor().getComponentUpgrades().stream().mapToDouble(upgrade -> upgrade.getComponentUpgradeType().getHeatRate()).sum()
                        + */(getFrameBodyParent().getChargeCell() == null ? 0 : getFrameBodyParent().getChargeCell().getUpgrades().stream().mapToDouble(upgrade -> upgrade.getComponentUpgradeType().getHeatRate()).sum());

// Using aggregateHeatRate to adjust stabilityRating, along with other factors like rechargeRate and voltageRating:
        double adjustmentFactor = (aggregateHeatRate + rechargeRate + voltageRating) * tier.getGigaHertz();
        this.stabilityRating = Math.max(0, this.stabilityRating - adjustmentFactor);

        // You will need a BASE_STABILITY value to work from
    }

    private void computeStabilityDropRate() {
        // 1. Base stability drop affected by voltageRating and heatRate.
        stabilityDropRate = (getFrameBodyParent().getMaxFrameAttachments()
                + (getFrameBodyParent().getEnergyCore().getComponentUpgrades()
                != null ? getFrameBodyParent().getEnergyCore().getComponentUpgrades().size()
                : 0) + /*(getFrameBodyParent().getCoreProcessor().getComponentUpgrades()
                != null ? getFrameBodyParent().getCoreProcessor().getUpgrades().size()
                : 0) + */(getFrameBodyParent().getChargeCell().getUpgrades()
                != null ? getFrameBodyParent().getChargeCell().getUpgrades().size()
                : 0)/* / (getFrameBodyParent().getCoreProcessor().getGigaHertz())*/ * (voltageRating / 10) + (getHeatRate() / 5));

        // 2. Additional drop from componentUpgrades.
        for (ComponentUpgrade upgrade : componentUpgrades) {
            // Let's assume each upgrade has a method called getStabilityDropModifier() that returns a factor by which
            // it modifies the stability drop rate. Add this value to the stabilityDropRate.
            stabilityDropRate *= upgrade.getGrade().getStabilityMultiplier();
        }

        // 3. Impact of current output.
        stabilityDropRate += rarity.getOutputRateMultiplier() * outputEnergyRate;

        // 4. Impact of rechargeRate when constantCharge upgrade is initiated.
        // Check for the existence of a constantCharge upgrade in componentUpgrades.
        boolean hasConstantCharge = componentUpgrades.stream()
                .anyMatch(upgrade -> upgrade instanceof ConstantCharge);
        if (hasConstantCharge) {
            stabilityDropRate += rarity.getChargeRateMultiplier() * rechargeRate;
        }

        // 5. Impact of the difference between sustainTime and maxSustainTime.
        double sustainTimeDifferenceFactor = (maxSustainRate - sustainTime) / maxSustainRate;
        stabilityDropRate += sustainTime * sustainTimeDifferenceFactor;

        // Ensure stabilityDropRate remains in bounds (0-100), if needed.
        stabilityDropRate = Math.min(100, Math.max(0, stabilityDropRate));
    }

    private void computeVoltageRating() {
        // Original computation for base voltage
        voltageRating = 1.2 * rarity.getOutputRateMultiplier();

        // Add voltage from OverVolt upgrade if it exists in ChargeCell
        for(ComponentUpgrade<?> upgrade : frameBodyParent.getChargeCell().getUpgrades()) {
            if(upgrade instanceof OverVolt) {
                voltageRating += ((OverVolt) upgrade).getVoltageMultiplier();
                break; // Assuming there's only one instance of OverVolt. If not, remove this break.
            }
        }
    }

    private void computeVoltageThreshold() {
        voltageThreshold *= rarity.getThresholdMultiplier();

        // Add voltage from OverVolt upgrade if it exists in ChargeCell
        for(ComponentUpgrade<?> upgrade : frameBodyParent.getChargeCell().getUpgrades()) {
            if(upgrade instanceof OverVolt) {
                voltageThreshold += ((OverVolt) upgrade).getVoltageMultiplier();
                break; // Assuming there's only one instance of OverVolt. If not, remove this break.
            }
        }
    }

    private void computeOutputEnergyRate() {
        outputEnergyRate = tier.getEnergyOutputRate() * rarity.getOutputRateMultiplier() * rarity.getThresholdMultiplier();
    }

    private void computeCoreEnergyCapacitance() {
        coreEnergyCapacitance = tier.getCapacitance();
        // Adjust based on componentUpgrades
    }

    private void computeOutputEnergyRateThreshold() {
        // Adjust based on componentUpgrades
        outputEnergyRateThreshold = tier.getEnergyOutputRate();
    }

    private void computeSustainTime() {
        sustainTime = coreEnergyCapacitance / outputEnergyRate;
    }

    private void computeMaxSustainRate() {
        maxSustainRate = maxSustainRate + (maxSustainRate * rarity.getThresholdMultiplier() + tier.getLadder());
        // Adjust based on componentUpgrades
    }

    private void computeIdleDrawRate() {
        idleDrawRate = getIdleDrawRate() + (componentUpgrades.size() * tier.getGigaHertz()) ;
    }

    private void computeHeatRate() {
        setHeatRate(getHeatRate() * (voltageRating / voltageThreshold) * (sustainTime / maxSustainRate));
    }

    private void computeHeatStabilityThreshold() {
        heatStabilityThreshold = tier.getHeatRate() * rarity.getThresholdMultiplier();
        // Adjust based on componentUpgrades
    }

    public void addUpgrade(ComponentUpgrade upgrade) {
        if (componentUpgrades.size() >= upgradeLimit) {
            System.out.println("upgrade limit hit");
            return;
        }
        componentUpgrades.add(upgrade);
        computeAttributes();  // Recompute after adding an upgrade
    }

    public void removeUpgrade(ComponentUpgrade upgrade) {
        if (componentUpgrades.size() == 0) {
            System.out.println("you have no more upgrades to remove");
            computeAttributes();
            return;
        }
        componentUpgrades.remove(upgrade);
        computeAttributes();  // Recompute after removing an upgrade
    }

}
