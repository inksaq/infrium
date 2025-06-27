package com.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import lombok.Getter;
import lombok.Setter;
import com.infrium.game.components.weapon.energy.components.core.CoreComponent;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentLogger;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Setter
@Getter
public class EnergyCore extends CoreComponent{

    private FrameBody frameBodyParent;
    private UUID uuid;
    private EnergyCore energyCore;

    private int lifespan; /*Base lifespan is 2 years(in seconds),
     each added component to the Body reduces the lifespan on each component with lifespan(processor and chargecell(with different lifespans)
      but same calculation effecting all)
      lifespan is effected by Tier, Grade and Rarity*/
    private int coreEnergyCapacitance; // capacitance is a set value(altered by overload component) and is where energy is taken when fired, no matter max sustain time, if output energyrate depletes capacitance before sustain time is reached, it will weaken output(only output recharge delay(until max sustain is hit))
    private int idleDrawRate; // calculates corecomponents and upgrades added, and takes charge from chargecell capacitance for the count of added components

    //    @Getter @Setter private double rechargeDelay; // charging delay between max sustain and being able to output energy, cooldown(extra delay) may effect recharge rate and also upgrades, quickcharge, constantcharge may affect it too.
    private int rechargeRate; // Calculates rate at which energycore charges, affected by stability rating and heatRate and cooldown(cooldown in cooldown may not fire and half recharge rate with addition of extra recharge delay)
//    @Getter @Setter private double stabilityRating; // percentage value 0 - 100.00% indicating processor stability with upgradecomponents
//    @Getter @Setter private double stabilityDropRate; // calculated by taking into account, voltage rating, energyoutput rate, maxsustaintime, heatrate to generate a value when sustainTime increases to also decrease stability reducing it from stabilityRating overtime, cooldowns affect this too
//    @Getter @Setter private double voltageRating; //Default is 1.05 affected by overclocking (calculated by taking into account(tier, rarity, grade) and multiplying by voltage rating(voltageRating can be affected by adding overvolt or supervolt and going under with undervolt)
//    @Getter @Setter private double voltageThreshold; // default is (Tier*(Rarity/2) * Grade * voltage rating) * voltageRating
    private int outputEnergyRate; // output rate is calculated by having a base output rate calculated from(tier,grade,rarity) and them modifiers are added by the likes of overvolt,
//    @Getter @Setter private double outputEnergyRateThreshold; // threshold is calculated by overload(requires overvolt of chargecell) and underload(no min requirement) core component upgrades
//    @Getter @Setter private double sustainTime; // sustain time is counted when user holds down click, it draws energy from coreEnergyCapactiance and maintained by outputEnergyRate
//    @Getter @Setter private double maxSustainRate; // calculated by added core component upgrades added(overload(requires chargecell to have overvolt),superload(requires overcharge,superclock and supervolt to achieve superload((, No requirement for underload))
    private int heatRate; // calculates idle heat, all corecomponents and upgrades create a baseline, then when energyoutput happens, calculated by getting voltage rating and distance to voltage threshold, sustain and distance to max sustain(The need to cooldown)
//    @Getter @Setter private double heatStabilityThreshold; // Heat Threshold before stability drop rate increases affecting stabilitiy rating, modified by framebody heat exchanging attachments

    private Set<ComponentUpgrade<?>> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
    private Integer upgradeLimit;

//
//    private ItemStack cloneItem() {
//        ItemStack item = new ItemStack(Material.DIAMOND);
//        ItemMeta im = applyMeta(item);
//        item.setItemMeta(im);
//        return item;
//    }
//
//
//
//
//
//    private ItemMeta applyMeta(ItemStack itemStack) {
//        ItemMeta meta = itemStack.getItemMeta();
//        meta.displayName(Component.text("Energy Core"));
//        meta.lore(getEnergyCoreLore());
//        return meta;
//    }

    public EnergyCore(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.ENERGY_CORE);
        energyCore = this;
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


    public EnergyCore(FrameBody frameBodyParent,
            Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.ENERGY_CORE);
        energyCore = this;
        this.frameBodyParent = frameBodyParent;
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

    public ItemStack createItemStack() {
        logDebug("Creating ItemStack for FrameBody");
        ItemStack item = new ItemStack(Material.NETHERITE_HOE); // You can change this to whatever material represents your FrameBody
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Energy Core");
        meta.setLore(getEnergyCoreLore());
        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.mergeCompound(this.serializeToNBT());
        });

        logInfo("Created ItemStack for EnergyCore: " + uuid);
        return item;
    }

    public static EnergyCore fromItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            CoreComponentLogger.warning(CoreComponentType.ENERGY_CORE, "Invalid ItemStack for FrameBody creation");
            return null;
        }

        return NBT.get(item, (nbt) -> {
            if (nbt.hasTag("uuid")) {
                return deserializeFromNBT((NBTCompound) nbt);
            } else {
                CoreComponentLogger.warning(CoreComponentType.ENERGY_CORE, "ItemStack does not contain FrameBody NBT data");
                return null;
            }
        });
    }




/*    public List<String> getEnergyCoreLore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Energy Core");
        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GRAY + "UUID: " + ChatColor.WHITE + energyCore.getUuid());
        lore.add(ChatColor.GRAY + "Tier: " + ChatColor.WHITE + energyCore.getTier());
        lore.add(ChatColor.GRAY + "Grade: " + ChatColor.WHITE + energyCore.getGrade());
        lore.add(ChatColor.GRAY + "Rarity: " + ChatColor.WHITE + energyCore.getRarity());
        lore.add(ChatColor.GRAY + "Lifespan: " + ChatColor.WHITE + energyCore.getLifespan() + "s");
        lore.add(ChatColor.GRAY + "Capacitance: " + ChatColor.WHITE + energyCore.getCoreEnergyCapacitance() + "u");
        lore.add(ChatColor.GRAY + "Idle Draw: " + ChatColor.WHITE + energyCore.getIdleDrawRate() + "u/s");
        lore.add(ChatColor.GRAY + "Recharge: " + ChatColor.WHITE + energyCore.getRechargeRate() + "u/s");
        lore.add(ChatColor.GRAY + "Output Rate: " + ChatColor.WHITE + energyCore.getOutputEnergyRate() + "u/s");
        lore.add(ChatColor.GRAY + "Heat Rate: " + ChatColor.WHITE + energyCore.getHeatRate() + "°C/s");
        lore.add(ChatColor.GRAY + "Upgrades: "  + (energyCore.getComponentUpgrades() != null ? "[" + energyCore.getComponentUpgrades().size() + "/" + energyCore.getUpgradeLimit() +"] (click for upgrades)" : "[0/0] (click for upgrades)"));
        if (energyCore.getUpgrades() != null) {
            energyCore.getUpgrades().forEach(upgrade -> lore.add(ChatColor.GRAY + "  - " + upgrade.getComponentUpgradeType().name()));
        }
        lore.add(ChatColor.GRAY + "---------------------");

        return lore;
    }*/



    public List<String> getEnergyCoreLore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Energy Core");
        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GRAY + "UUID: " + ChatColor.WHITE + energyCore.getUuid().toString());
        lore.add(ChatColor.GRAY + "Tier: " + ChatColor.WHITE + energyCore.getTier().toString());
        lore.add(ChatColor.GRAY + "Grade: " + ChatColor.WHITE + energyCore.getGrade().toString());
        lore.add(ChatColor.GRAY + "Rarity: " + ChatColor.WHITE + energyCore.getRarity().toString());
        lore.add(ChatColor.GRAY + "Lifespan: " + ChatColor.WHITE + energyCore.getLifespan() + "s");
        lore.add(ChatColor.GRAY + "Capacitance: " + ChatColor.WHITE + energyCore.getCoreEnergyCapacitance() + "u");
        lore.add(ChatColor.GRAY + "Idle Draw: " + ChatColor.WHITE + energyCore.getIdleDrawRate() + "u/s");
        lore.add(ChatColor.GRAY + "Recharge: " + ChatColor.WHITE + energyCore.getRechargeRate() + "u/s");
        lore.add(ChatColor.GRAY + "Output Rate: " + ChatColor.WHITE + energyCore.getOutputEnergyRate() + "u/s");
        lore.add(ChatColor.GRAY + "Heat Rate: " + ChatColor.WHITE + energyCore.getHeatRate() + "°C/s");
        lore.add(ChatColor.GRAY + "Upgrades: " + (energyCore.getComponentUpgrades() != null ? "[" + energyCore.getComponentUpgrades().size() + "/" + energyCore.getUpgradeLimit() + "] (click for upgrades)" : "[0/0] (click for upgrades)"));
        if (energyCore.getComponentUpgrades() != null) {
            for (ComponentUpgrade<?> upgrade : energyCore.getComponentUpgrades()) {
                lore.add(ChatColor.GRAY + "  - " + upgrade.getComponentUpgradeType().name());
            }
        }
        lore.add(ChatColor.GRAY + "---------------------");

        return lore;
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
//        computeIdleDrawRate();
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
        if (frameBodyParent.getCoreProcessor().getCoreProcessor() == null) return;
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
        if (componentUpgrades == null) componentUpgrades = new HashSet<>(upgradeLimit);
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
    @Override
    public NBTCompound serializeToNBT() {
        logDebug("Serializing EnergyCore to NBT");
        NBTCompound nbt = null;
        try {
            nbt = (NBTCompound) NBT.createNBTObject();
            if (nbt == null) {
                logSevere("Failed to create NBT object for EnergyCore");
                return null;
            }

            nbt.setString("uuid", uuid.toString());
            nbt.setInteger("coreEnergyCapacitance", coreEnergyCapacitance);
            nbt.setInteger("idleDrawRate", idleDrawRate);
            nbt.setInteger("rechargeRate", rechargeRate);
            nbt.setInteger("heatRate", heatRate);
            nbt.setInteger("lifespan", lifespan);
            nbt.setInteger("upgradeLimit", upgradeLimit);
            nbt.setString("tier", getTier().name());
            nbt.setString("grade", getGrade().name());
            nbt.setString("rarity", getRarity().name());

            NBTCompound upgradesNBT = nbt.getOrCreateCompound("componentUpgrades");
            int index = 0;
            if (componentUpgrades != null) {

                for (ComponentUpgrade<?> upgrade : componentUpgrades) {
                    upgradesNBT.getOrCreateCompound("upgrade_" + index).mergeCompound(upgrade.serialize());
                    index++;
                }
            }

            logInfo("EnergyCore serialized to NBT");
        } catch (Exception e) {
            logSevere("Error serializing EnergyCore to NBT: " + e.getMessage());
            e.printStackTrace();
        }
        return nbt;
    }


    /**
     * Deserialize an EnergyCore from an NBTCompound.
     *
     * @param nbt the source NBTCompound.
     * @return a new EnergyCore constructed from the given NBT data.
     */
    public static EnergyCore deserializeFromNBT(NBTCompound nbt) {
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
        ReadWriteNBTCompoundList upgrades = nbt.getCompoundList("componentUpgrades");
        upgrades.forEach(readWriteNBT -> core.componentUpgrades.add(ComponentUpgrade.deserialize(readWriteNBT)));
        for (ReadWriteNBT upgradeNBT : upgrades) {
            ComponentUpgrade<?> upgrade = ComponentUpgrade.deserialize(upgradeNBT); // Assumes ComponentUpgrade has a static deserialize method
            core.componentUpgrades.add(upgrade);
        }

        return core;
    }

    public static class NBTHandler {

        // Keys for EnergyCore's NBT data
        private static final String CAPACITY_KEY = "capacity";
        private static final String CURRENT_ENERGY_KEY = "currentEnergy";

        public static ReadWriteNBT serializeToNBT(EnergyCore energyCore) {
            ReadWriteNBT nbt = NBT.createNBTObject();
            nbt.setInteger("tier", energyCore.getTier().getLadder());
            nbt.setInteger("grade", energyCore.getGrade().getGradeLadder());
            nbt.setInteger("rarity", energyCore.getRarity().getLadder());
            nbt.setInteger(CAPACITY_KEY, energyCore.getCoreEnergyCapacitance());
            nbt.setInteger(CURRENT_ENERGY_KEY, energyCore.getOutputEnergyRate());
            return nbt;
        }

        public static EnergyCore deserializeFromNBT(ReadWriteNBT nbt) {
            var tier = nbt.getInteger("tier");
            var rarity = nbt.getInteger("rarity");
            var grade = nbt.getInteger("grade");
            EnergyCore energyCore = new EnergyCore(Rarity.getRarityLadder(rarity), Grade.getGradeLadder(grade), Tier.getTierLadder(tier));
            energyCore.setCoreEnergyCapacitance(nbt.getInteger(CAPACITY_KEY));
            energyCore.setOutputEnergyRate(nbt.getInteger(CURRENT_ENERGY_KEY));
            return energyCore;
        }
    }

}
