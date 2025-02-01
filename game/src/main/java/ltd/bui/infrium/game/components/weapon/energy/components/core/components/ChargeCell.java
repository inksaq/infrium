package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentLogger;
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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    private Set<ComponentUpgrade<?>> componentUpgrades ; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)
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


    public ChargeCell(FrameBody frameBodyParent,Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.CHARGE_CELL);
        chargeCell = this;
        this.frameBodyParent = frameBodyParent;
        this.uuid = UUID.randomUUID();
        this.componentUpgrades = new HashSet<>();

        if (capacity == 0) capacity = tier.getCapacitance();
        if (lifespan == 0) lifespan = grade.getLifespan();
        this.upgradeLimit = (Integer) rarity.getComponentUpgradeLimit();
        this.currentChargeRate = tier.getRechargeRate();
        this.currentOutputRate = tier.getEnergyOutputRate();
        this.heatRate = tier.getHeatRate();
    }

    public ChargeCell(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.CHARGE_CELL);
        chargeCell = this;
        this.uuid = UUID.randomUUID();
        this.componentUpgrades = new HashSet<>();
        if (capacity == 0) capacity = tier.getCapacitance();
        if (lifespan == 0) lifespan = grade.getLifespan();
        this.upgradeLimit = (Integer) rarity.getComponentUpgradeLimit();
        this.currentChargeRate = tier.getRechargeRate();
        this.currentOutputRate = tier.getEnergyOutputRate();
        this.heatRate = tier.getHeatRate();
    }

    @Override
    public NBTCompound serializeToNBT() {
        logDebug("Serializing ChargeCell to NBT");
        NBTCompound nbt = (NBTCompound) NBT.createNBTObject();
        nbt.setString("uuid", uuid.toString());
        nbt.setDouble("lifespan", lifespan);
        nbt.setInteger("capacity", capacity);
        nbt.setInteger("currentChargeRate", currentChargeRate);
        nbt.setInteger("currentOutputRate", currentOutputRate);
        nbt.setInteger("heatRate", heatRate);
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

        logInfo("ChargeCell serialized to NBT");
        return nbt;
    }


    public static ChargeCell deserializeFromNBT(NBTCompound nbt) {
        CoreComponentLogger.info(CoreComponentType.CHARGE_CELL, "Deserializing ChargeCell from NBT");

        Grade grade = Grade.valueOf(nbt.getString("grade"));
        Rarity rarity = Rarity.valueOf(nbt.getString("rarity"));
        Tier tier = Tier.valueOf(nbt.getString("tier"));

        ChargeCell chargeCell = new ChargeCell(rarity, grade, tier);
        chargeCell.setUuid(UUID.fromString(nbt.getString("uuid")));
        chargeCell.setLifespan(nbt.getDouble("lifespan"));
        chargeCell.setCapacity(nbt.getInteger("capacity"));
        chargeCell.setCurrentChargeRate(nbt.getInteger("currentChargeRate"));
        chargeCell.setCurrentOutputRate(nbt.getInteger("currentOutputRate"));
        chargeCell.setHeatRate(nbt.getInteger("heatRate"));
        chargeCell.setUpgradeLimit(nbt.getInteger("upgradeLimit"));

        NBTCompound upgradesNBT = nbt.getCompound("componentUpgrades");
        for (String key : upgradesNBT.getKeys()) {
            ComponentUpgrade<?> upgrade = ComponentUpgrade.deserialize(upgradesNBT.getCompound(key));
            if (upgrade != null) {
                chargeCell.addUpgrade(upgrade);
            }
        }

        CoreComponentLogger.info(CoreComponentType.CHARGE_CELL, "ChargeCell deserialized from NBT");
        return chargeCell;
    }

    public ItemStack createItemStack() {
        logDebug("Creating ItemStack for FrameBody");
        ItemStack item = new ItemStack(Material.NETHERITE_HOE); // You can change this to whatever material represents your FrameBody
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Frame Body");
        meta.setLore(getChargeCellLore());
        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.mergeCompound(this.serializeToNBT());
        });

        logInfo("Created ItemStack for FrameBody: " + uuid);
        return item;
    }

    public static ChargeCell fromItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            CoreComponentLogger.warning(CoreComponentType.CHARGE_CELL, "Invalid ItemStack for FrameBody creation");
            return null;
        }

        return NBT.get(item, (nbt) -> {
            if (nbt.hasTag("uuid")) {
                return deserializeFromNBT((NBTCompound) nbt);
            } else {
                CoreComponentLogger.warning(CoreComponentType.CHARGE_CELL, "ItemStack does not contain FrameBody NBT data");
                return null;
            }
        });
    }

    public void addUpgrade(ComponentUpgrade<? extends CoreComponent> upgrade) {
        if (componentUpgrades == null) {
            componentUpgrades = new HashSet<>();
        }

        if (componentUpgrades.size() >= upgradeLimit) {
            logWarning("Cannot add upgrade. Upgrade limit reached.");
            return;
        }

        componentUpgrades.add(upgrade);
//        upgrade.setAppliedTo(this);
        logInfo("Added upgrade: " + upgrade.getComponentUpgradeType());
    }

    public void removeUpgrade(ComponentUpgrade<? extends CoreComponent> upgrade) {
        if (componentUpgrades.remove(upgrade)) {
            upgrade.unSetAppliedTo();
            logInfo("Removed upgrade: " + upgrade.getComponentUpgradeType());
        } else {
            logWarning("Attempted to remove non-existent upgrade: " + upgrade.getComponentUpgradeType());
        }
    }

    public void onTick(){
        computeAttributes();
        applyDegradation();
        if (frameBodyParent != null && frameBodyParent.getChargeCell() != null && frameBodyParent.getChargeCell().chargeCell != null) {
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
        logDebug("Attributes computed");
    }
        private void applyDegradation() {
            // Implement degradation logic
            logDebug("Degradation applied - Capacity: " + capacity + ", Lifespan: " + lifespan);
        }

        public void chargeCore() {
            logInfo("Starting chargeCore");
            // Implement core charging logic
            logInfo("Ending chargeCore - Energy transferred: " /* Add transferred energy amount here */);
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
