package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.handler.NBTHandlers;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class EnergyCore extends CoreComponent {

    @Getter private FrameBody frameBodyParent;
    @Getter @Setter private UUID uuid;

    @Getter @Setter private int lifespan; /*Base lifespan is 2 years(in seconds),
     each added component to the Body reduces the lifespan on each component with lifespan(processor and chargecell(with different lifespans)
      but same calculation effecting all)
      lifespan is effected by Tier, Grade and Rarity*/
    @Getter @Setter private int coreEnergyCapacitance; // capacitance is a set value(altered by overload component) and is where energy is taken when fired, no matter max sustain time, if output energyrate depletes capacitance before sustain time is reached, it will weaken output(only output recharge delay(until max sustain is hit))
    @Getter @Setter private int idleDrawRate; // calculates corecomponents and upgrades added, and takes charge from chargecell capacitance for the count of added components

    //    @Getter @Setter private double rechargeDelay; // charging delay between max sustain and being able to output energy, cooldown(extra delay) may effect recharge rate and also upgrades, quickcharge, constantcharge may affect it too.
    @Getter @Setter private int rechargeRate; // Calculates rate at which energycore charges, affected by stability rating and heatRate and cooldown(cooldown in cooldown may not fire and half recharge rate with addition of extra recharge delay)
//    @Getter @Setter private double stabilityRating; // percentage value 0 - 100.00% indicating processor stability with upgradecomponents
//    @Getter @Setter private double stabilityDropRate; // calculated by taking into account, voltage rating, energyoutput rate, maxsustaintime, heatrate to generate a value when sustainTime increases to also decrease stability reducing it from stabilityRating overtime, cooldowns affect this too
//    @Getter @Setter private double voltageRating; //Default is 1.05 affected by overclocking (calculated by taking into account(tier, rarity, grade) and multiplying by voltage rating(voltageRating can be affected by adding overvolt or supervolt and going under with undervolt)
//    @Getter @Setter private double voltageThreshold; // default is (Tier*(Rarity/2) * Grade * voltage rating) * voltageRating
    @Getter @Setter private int outputEnergyRate; // output rate is calculated by having a base output rate calculated from(tier,grade,rarity) and them modifiers are added by the likes of overvolt,
//    @Getter @Setter private double outputEnergyRateThreshold; // threshold is calculated by overload(requires overvolt of chargecell) and underload(no min requirement) core component upgrades
//    @Getter @Setter private double sustainTime; // sustain time is counted when user holds down click, it draws energy from coreEnergyCapactiance and maintained by outputEnergyRate
//    @Getter @Setter private double maxSustainRate; // calculated by added core component upgrades added(overload(requires chargecell to have overvolt),superload(requires overcharge,superclock and supervolt to achieve superload((, No requirement for underload))
    @Getter @Setter private int heatRate; // calculates idle heat, all corecomponents and upgrades create a baseline, then when energyoutput happens, calculated by getting voltage rating and distance to voltage threshold, sustain and distance to max sustain(The need to cooldown)
//    @Getter @Setter private double heatStabilityThreshold; // Heat Threshold before stability drop rate increases affecting stabilitiy rating, modified by framebody heat exchanging attachments

    @Getter @Setter private Set<ComponentUpgrade<?>> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;


    public EnergyCore(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.ENERGY_CORE);
        this.uuid = UUID.randomUUID();
        if (lifespan == 0) lifespan = grade.getLifespan();
        this.upgradeLimit = rarity.getComponentUpgradeLimit();
        this.idleDrawRate = tier.getIdleDraw();
        this.coreEnergyCapacitance = tier.getCapacitance();
        this.heatRate = tier.getHeatRate();
        this.outputEnergyRate = tier.getEnergyOutputRate();
        this.rechargeRate = tier.getRechargeRate();
        this.upgradeLimit = 2;
//        this.sustainTime = 0;
//        this.stabilityRating = 100;
//        this.rechargeDelay = 2; //todo
    }

    public FrameBody getFrameBodyParent() {
        return this.frameBodyParent;
    }

    public void setFrameBodyParent(FrameBody frameBody) {
        this.frameBodyParent = frameBody;
    }
    public void onTick(){
        computeAttributes();
        applyDegredation();
    }

    private void applyDegredation() {
        // Variables
//        double lifespanDegradationFactor = 0.01; // 1% per tick
//        double sustainedOutputDegradation = sustainTime * outputEnergyRate / coreEnergyCapacitance;

        // Degradation from output energy rate and sustained output
//        lifespan -= sustainedOutputDegradation;

        // Degradation from idle draw rate
        lifespan -= idleDrawRate;

        // Apply a generic degradation factor to lifespan
//        lifespan -= (lifespan * lifespanDegradationFactor);

        // Ensure values don't go negative
        if (outputEnergyRate < 0) outputEnergyRate = 0;
        if (lifespan < 0) lifespan = 0;
//        if (sustainTime < 0) sustainTime = 0;
    }


    private void computeAttributes() {
        computeLifespan();
//        computeRechargeDelay();
//        computeRechargeRate();
//        computeStabilityRating();
//        computeStabilityDropRate();
//        computeVoltageRating();
//        computeVoltageThreshold();
        computeOutputEnergyRate();
        computeCoreEnergyCapacitance();
//        computeOutputEnergyRateThreshold();
//        computeSustainTime();
//        computeMaxSustainRate();
        computeIdleDrawRate();
        computeHeatRate();
//        computeHeatStabilityThreshold();
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
//        sustainTime += 1;  // Assuming this is called once per tick/unit-time
//        if(sustainTime > maxSustainRate) {
//            sustainTime = maxSustainRate;  // Ensure it doesn't exceed max
//        }
//        System.out.println("Updated sustainTime: " + sustainTime);

        // Heat generation
//        double voltageDifference = voltageRating - voltageThreshold;
//        double sustainDifference = sustainTime - maxSustainRate;
//        HeatRate += (voltageDifference + sustainDifference) / 2;
//        System.out.println("HeatRate after energy use: " + HeatRate);

        // Stability management
//        double heatImpact = (HeatRate > heatStabilityThreshold) ? 2 : 1; // e.g., doubles the drop rate when we cross the threshold
//        stabilityRating -= stabilityDropRate * heatImpact;
//        System.out.println("Updated stabilityRating: " + stabilityRating);
//
//        // Respect for the max sustained output
//        if(sustainTime == maxSustainRate) {
//            outputEnergyRate *= 0.5;  // e.g., Halve the output energy rate when sustain time is maxed out.
//            System.out.println("Sustain time maxed out. New outputEnergyRate: " + outputEnergyRate);
//        }

        // Ensure values don't go below their minimum limits
        if(coreEnergyCapacitance < 0) coreEnergyCapacitance = 0;
//        if(stabilityRating < 0) stabilityRating = 0;
        if(heatRate < 0) heatRate = 0;

        System.out.println("--- Ending expendEnergy ---");
    }


    private void computeIdleDrawRate() {
        if (frameBodyParent.getCoreProcessor() == null) return;
        idleDrawRate += frameBodyParent.getCoreProcessor().getGigaHertz();
    }

    private void computeHeatRate() {
        heatRate = tier.getHeatRate();
        if (frameBodyParent.getChargeCell() == null || frameBodyParent.getCoreProcessor() == null){
            return;
        }

        this.heatRate = frameBodyParent.getChargeCell().getHeatRate() + frameBodyParent.getCoreProcessor().getHeatRate() + heatRate;
    }

    private void computeLifespan() {
        lifespan -= componentUpgrades != null ? componentUpgrades.size() + idleDrawRate: idleDrawRate; // You may want a factor to degrade lifespan
    }

//    private void computeRechargeDelay() {
//        // sample calculation
//        rechargeDelay = (getRechargeDelay() / 100) + (getHeatRate() / 10);
//        // Loop through componentUpgrades to modify rechargeDelay based on their effects
//    }

//    private void computeRechargeRate() {
//        // sample calculation
//        rechargeRate = getRechargeRate() * (stabilityRating / 100) - (getHeatRate() / 10);
//        // Loop through componentUpgrades to modify rechargeRate based on their effects
//    }

//    private void computeStabilityRating() {
//        // basic idea
//        double aggregateHeatRate =
//                /*getFrameBodyParent().getCoreProcessor().getComponentUpgrades().stream().mapToDouble(upgrade -> upgrade.getComponentUpgradeType().getHeatRate()).sum()
//                        + */(getFrameBodyParent().getChargeCell() == null ? 0 : getFrameBodyParent().getChargeCell().getUpgrades().stream().mapToDouble(upgrade -> upgrade.getComponentUpgradeType().getHeatRate()).sum());
//
//// Using aggregateHeatRate to adjust stabilityRating, along with other factors like rechargeRate and voltageRating:
//        double adjustmentFactor = (aggregateHeatRate + rechargeRate + voltageRating) * tier.getGigaHertz();
//        this.stabilityRating = Math.max(0, this.stabilityRating - adjustmentFactor);
//
//        // You will need a BASE_STABILITY value to work from
//    }

//    private void computeStabilityDropRate() {
//        // 1. Base stability drop affected by voltageRating and heatRate.
//        stabilityDropRate = (getFrameBodyParent().getMaxFrameAttachments()
//                + (getFrameBodyParent().getEnergyCore().getComponentUpgrades()
//                != null ? getFrameBodyParent().getEnergyCore().getComponentUpgrades().size()
//                : 0) + /*(getFrameBodyParent().getCoreProcessor().getComponentUpgrades()
//                != null ? getFrameBodyParent().getCoreProcessor().getUpgrades().size()
//                : 0) + */(getFrameBodyParent().getChargeCell().getUpgrades()
//                != null ? getFrameBodyParent().getChargeCell().getUpgrades().size()
//                : 0)/* / (getFrameBodyParent().getCoreProcessor().getGigaHertz())*/ * (voltageRating / 10) + (getHeatRate() / 5));
//
//        // 2. Additional drop from componentUpgrades.
//        if (componentUpgrades != null) {
//            for (ComponentUpgrade upgrade : componentUpgrades) {
//                // Let's assume each upgrade has a method called getStabilityDropModifier() that returns a factor by which
//                // it modifies the stability drop rate. Add this value to the stabilityDropRate.
//                stabilityDropRate *= upgrade.getGrade().getStabilityMultiplier();
//            }
//            boolean hasConstantCharge = componentUpgrades.stream()
//                    .anyMatch(upgrade -> upgrade instanceof ConstantCharge);
//            if (hasConstantCharge) {
//                stabilityDropRate += rarity.getChargeRateMultiplier() * rechargeRate;
//            }
//        }
//
//        // 3. Impact of current output.
//        stabilityDropRate += rarity.getOutputRateMultiplier() * outputEnergyRate;
//
//        // 4. Impact of rechargeRate when constantCharge upgrade is initiated.
//        // Check for the existence of a constantCharge upgrade in componentUpgrades.
//
//        // 5. Impact of the difference between sustainTime and maxSustainTime.
//        double sustainTimeDifferenceFactor = (maxSustainRate - sustainTime) / maxSustainRate;
//        stabilityDropRate += sustainTime * sustainTimeDifferenceFactor;
//
//        // Ensure stabilityDropRate remains in bounds (0-100), if needed.
//        stabilityDropRate = Math.min(100, Math.max(0, stabilityDropRate));
//    }

//    private void computeVoltageRating() {
//        // Original computation for base voltage
//        voltageRating = 1.2 * rarity.getOutputRateMultiplier();
//
//        // Add voltage from OverVolt upgrade if it exists in ChargeCell
//        for(ComponentUpgrade<?> upgrade : frameBodyParent.getChargeCell().getUpgrades()) {
//            if(upgrade instanceof OverVolt) {
//                voltageRating += ((OverVolt) upgrade).getVoltageMultiplier();
//                break; // Assuming there's only one instance of OverVolt. If not, remove this break.
//            }
//        }
//    }
//
//    private void computeVoltageThreshold() {
//        voltageThreshold *= rarity.getThresholdMultiplier();
//
//        // Add voltage from OverVolt upgrade if it exists in ChargeCell
//        for(ComponentUpgrade<?> upgrade : frameBodyParent.getChargeCell().getUpgrades()) {
//            if(upgrade instanceof OverVolt) {
//                voltageThreshold += ((OverVolt) upgrade).getVoltageMultiplier();
//                break; // Assuming there's only one instance of OverVolt. If not, remove this break.
//            }
//        }
//    }

    private void computeOutputEnergyRate() {
        outputEnergyRate = tier.getEnergyOutputRate() * (int) (rarity.getOutputRateMultiplier() * rarity.getThresholdMultiplier());
    }

    private void computeCoreEnergyCapacitance() {
        coreEnergyCapacitance = tier.getCapacitance();
        // Adjust based on componentUpgrades
    }

//    private void computeOutputEnergyRateThreshold() {
//        // Adjust based on componentUpgrades
//        outputEnergyRateThreshold = tier.getEnergyOutputRate();
//    }
//
//    private void computeSustainTime() {
//        sustainTime = coreEnergyCapacitance / outputEnergyRate;
//    }
//
//    private void computeMaxSustainRate() {
//        maxSustainRate = maxSustainRate + (maxSustainRate * rarity.getThresholdMultiplier() + tier.getLadder());
//        // Adjust based on componentUpgrades
//    }
//
//    private void computeIdleDrawRate() {
//        idleDrawRate = getIdleDrawRate() + (componentUpgrades != null ? componentUpgrades.size(): 0 * tier.getGigaHertz()) ;
//    }
//
//    private void computeHeatRate() {
//        setHeatRate(getHeatRate() * (voltageRating / voltageThreshold) * (sustainTime / maxSustainRate));
//    }
//
//    private void computeHeatStabilityThreshold() {
//        heatStabilityThreshold = tier.getHeatRate() * rarity.getThresholdMultiplier();
//        // Adjust based on componentUpgrades
//    }

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

    /**
     * Serialize this EnergyCore into an NBTCompound.
     *
     * @return the serialized NBTCompound.
     */
    public NBTCompound serializeToNBT() {

        NBTCompound nbt = (NBTCompound) NBT.createNBTObject().get("energyCore", NBTHandlers.STORE_READABLE_TAG);
        nbt.setString("uuid", uuid.toString());
        nbt.setInteger("lifespan", lifespan);
        nbt.setInteger("coreEnergyCapacitance", coreEnergyCapacitance);
        nbt.setInteger("idleDrawRate", idleDrawRate);
        nbt.setInteger("rechargeRate", rechargeRate);
        nbt.setInteger("outputEnergyRate", outputEnergyRate);
        nbt.setInteger("heatRate", heatRate);

        // For enums and other complex attributes, you can further serialize them.
        nbt.setString("rarity", rarity.name());
        nbt.setString("grade", grade.name());
        nbt.setString("tier", tier.name());

        // Serialize componentUpgrades (assuming you have a way to serialize/deserialize each ComponentUpgrade)
//        ReadWriteNBTCompoundList upgrades = (ReadWriteNBTCompoundList) nbt.getOrCreateCompound("componentUpgrades");
//        componentUpgrades.forEach(componentUpgrade -> upgrades.addCompound().mergeCompound(componentUpgrade.serialize()));
//        nbt.set("componentUpgrades", upgrades);

        return nbt;
    }


    /**
     * Deserialize an EnergyCore from an NBTCompound.
     *
     * @param nbt the source NBTCompound.
     * @return a new EnergyCore constructed from the given NBT data.
     */
    public EnergyCore deserializeFromNBT(NBTCompound nbt) {
        Rarity rarity = Rarity.valueOf(nbt.getString("rarity"));
        Grade grade = Grade.valueOf(nbt.getString("grade"));
        Tier tier = Tier.valueOf(nbt.getString("tier"));

        EnergyCore core = new EnergyCore(rarity, grade, tier);
        core.setUuid(UUID.fromString(nbt.getString("uuid")));
        core.setLifespan(nbt.getInteger("lifespan"));
        core.setCoreEnergyCapacitance(nbt.getInteger("coreEnergyCapacitance"));
        core.setIdleDrawRate(nbt.getInteger("idleDrawRate"));
        core.setRechargeRate(nbt.getInteger("rechargeRate"));
        core.setOutputEnergyRate(nbt.getInteger("outputEnergyRate"));
        core.setHeatRate(nbt.getInteger("heatRate"));

        // Deserialize componentUpgrades
//        ReadWriteNBTCompoundList upgrades = nbt.getCompoundList("componentUpgrades");
//        upgrades.forEach(readWriteNBT -> core.componentUpgrades.add(ComponentUpgrade.deserialize(readWriteNBT)));
//        for (ReadWriteNBT upgradeNBT : upgrades) {
//            ComponentUpgrade<?> upgrade = ComponentUpgrade.deserialize(upgradeNBT); // Assumes ComponentUpgrade has a static deserialize method
//            core.componentUpgrades.add(upgrade);
//        }

        return core;
    }

    public static class NBTHandler {

        // Keys for EnergyCore's NBT data
        private static final String CAPACITY_KEY = "capacity";
        private static final String CURRENT_ENERGY_KEY = "currentEnergy";

        public static ReadWriteNBT serializeToNBT(EnergyCore energyCore) {
            ReadWriteNBT nbt = NBT.createNBTObject();
            nbt.setInteger(CAPACITY_KEY, energyCore.getCoreEnergyCapacitance());
            nbt.setInteger(CURRENT_ENERGY_KEY, energyCore.getOutputEnergyRate());
            return nbt;
        }

        public static EnergyCore deserializeFromNBT(ReadWriteNBT nbt) {
            var tier = nbt.getInteger("grade");
            EnergyCore energyCore = new EnergyCore();
            energyCore.setCoreEnergyCapacitance(nbt.getInteger(CAPACITY_KEY));
            energyCore.setOutputEnergyRate(nbt.getInteger(CURRENT_ENERGY_KEY));
            return energyCore;
        }
    }

}
