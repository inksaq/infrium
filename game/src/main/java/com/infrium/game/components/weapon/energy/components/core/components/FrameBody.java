package com.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.*;
import lombok.Getter;
import lombok.Setter;
import com.infrium.game.components.weapon.energy.components.attachments.FrameAttachment;
import com.infrium.game.components.weapon.energy.components.core.CoreComponent;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentLogger;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import com.infrium.game.components.weapon.registry.WeaponRegistry;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Function;

@Setter
@Getter
public class FrameBody extends CoreComponent {

    @Getter private final UUID frameUUID;
    @Getter @Setter private int lifespan;
    @Getter @Setter private int maxFrameAttachments;
    @Getter private final Map<CoreComponentType, CoreComponent> components;
    @Getter private final Set<FrameAttachment> frameAttachments;

    public FrameBody(Grade grade, Rarity rarity, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.FRAME_BODY);
        this.frameUUID = UUID.randomUUID();
        this.lifespan = grade.getLifespan();
        this.maxFrameAttachments = calculateMaxAttachments(grade, rarity, tier);
        this.components = new EnumMap<>(CoreComponentType.class);
        this.frameAttachments = new HashSet<>();
        logInfo("Created new FrameBody: " + this.frameUUID);
    }

    private int calculateMaxAttachments(Grade grade, Rarity rarity, Tier tier) {
        int baseAttachments = 3;
        int gradeBonus = grade.getGradeLadder();
        int rarityBonus = rarity.getLadder();
        int tierBonus = tier.getLadder();
        return baseAttachments + gradeBonus + rarityBonus + tierBonus;
    }

    //COMPONENTS
    public void addComponent(CoreComponent component) {
        if (component != null) {
            components.put(component.getComponentType(), component);
            logInfo("Added component: " + component.getComponentType());
        } else {
            logWarning("Attempted to add null component");
        }
    }

    public void removeComponent(CoreComponentType type) {
        CoreComponent removed = components.remove(type);
        if (removed != null) {
            logInfo("Removed component: " + type);
        } else {
            logWarning("Attempted to remove non-existent component: " + type);
        }
    }

    public <T extends CoreComponent> T getComponent(CoreComponentType type) {
        return (T) components.get(type);
    }

    public CoreProcessor getCoreProcessor() {
        return getComponent(CoreComponentType.CORE_PROCESSOR);
    }

    public ChargeCell getChargeCell() {
        return getComponent(CoreComponentType.CHARGE_CELL);
    }

    public EnergyCore getEnergyCore() {
        return getComponent(CoreComponentType.ENERGY_CORE);
    }

    public LensConduit getLensConduit() {
        return getComponent(CoreComponentType.LENS_CONDUIT);
    }

    //ATTACHMENTS
    public void addFrameAttachment(FrameAttachment attachment) {
        if (frameAttachments.size() < maxFrameAttachments) {
            frameAttachments.add(attachment);
            logInfo("Added frame attachment: " + attachment.getClass().getSimpleName());
        } else {
            logWarning("Cannot add more attachments. Max limit reached.");
        }
    }

    public void removeFrameAttachment(FrameAttachment attachment) {
        if (frameAttachments.remove(attachment)) {
            logInfo("Removed frame attachment: " + attachment.getClass().getSimpleName());
        } else {
            logWarning("Attempted to remove non-existent frame attachment");
        }
    }

    public void updateRegistry(FrameBody frameBody) {
        WeaponRegistry.getInstance().updateWeapon(frameBody);
    }

    @Override
    public void addUpgrade(ComponentUpgrade<? extends CoreComponent> upgrade) {
        if (upgrade != null) {
            componentUpgrades.add(upgrade);
            logInfo("Added upgrade: " + upgrade.getComponentUpgradeType());
        } else {
            logWarning("Attempted to add null upgrade");
        }
    }

    //LOGIC
    @Override
    public void onTick() {
        logDebug("FrameBody tick started");
        components.values().forEach(CoreComponent::onTick);
        applyDegradation();
        logDebug("FrameBody tick completed");
    }

    private void applyDegradation() {
        int degradation = calculateDegradation();
        lifespan -= degradation;
        if (lifespan < 0) lifespan = 0;
        logDebug("Applied degradation: " + degradation + ". New lifespan: " + lifespan);
    }

    //CALCULATIONS

    private int calculateDegradation() {
        int baseDegradation = 1;
        int componentFactor = components.size();
        int attachmentFactor = frameAttachments.size();
        return baseDegradation + componentFactor + attachmentFactor;
    }

    //LORE

    public List<String> getFrameBodyLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Frame Body");
        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GRAY + "UUID: " + ChatColor.WHITE + frameUUID);
        lore.add(ChatColor.GRAY + "Tier: " + ChatColor.WHITE + getTier().getTierFormat());
        lore.add(ChatColor.GRAY + "Grade: " + ChatColor.WHITE + getGrade().getGradeFormat());
        lore.add(ChatColor.GRAY + "Rarity: " + ChatColor.WHITE + getRarity().getRarityFormat());
        lore.add(ChatColor.GRAY + "Lifespan: " + ChatColor.WHITE + lifespan + "s");
        lore.add(ChatColor.GRAY + "Attachments: " + ChatColor.WHITE + "[" + frameAttachments.size() + "/" + maxFrameAttachments + "]");

        lore.add(ChatColor.GRAY + "Components:");
        for (Map.Entry<CoreComponentType, CoreComponent> entry : components.entrySet()) {
            CoreComponent component = entry.getValue();
            lore.add(ChatColor.GRAY + "  - " + entry.getKey() + ": " + component.getTier().getTierFormat() + " " + component.getGrade().getGradeFormat() + component.getRarity().getRarityFormat());
        }

        lore.add(ChatColor.GRAY + "Frame Attachments:");
        for (FrameAttachment attachment : frameAttachments) {
            lore.add(ChatColor.GRAY + "  - " + attachment.getFrameAttachmentType().name() + " (" + attachment.getTier().getTierFormat() + " " + attachment.getGrade().getGradeFormat() + attachment.getRarity().getRarityFormat() + ")");
        }

        lore.add(ChatColor.GRAY + "---------------------");
        return lore;
    }

    public NBTCompound serializeToNBT() {
        logDebug("Serializing FrameBody to NBT");
        NBTCompound nbt = (NBTCompound) NBT.createNBTObject();
        nbt.setString("uuid", frameUUID.toString());
        nbt.setInteger("lifespan", lifespan);
        nbt.setInteger("maxFrameAttachments", maxFrameAttachments);
        nbt.setString("tier", getTier().name());
        nbt.setString("grade", getGrade().name());
        nbt.setString("rarity", getRarity().name());

        NBTCompound componentsNBT = nbt.getOrCreateCompound("components");
        for (Map.Entry<CoreComponentType, CoreComponent> entry : components.entrySet()) {
            componentsNBT.getOrCreateCompound(entry.getKey().name()).mergeCompound(entry.getValue().serializeToNBT());
        }

        NBTCompound attachmentsNBT = nbt.getOrCreateCompound("frameAttachments");
        int index = 0;
        for (FrameAttachment attachment : frameAttachments) {
            attachmentsNBT.getOrCreateCompound("attachment_" + index).mergeCompound(attachment.serializeToNBT());
            index++;
        }

        logInfo("FrameBody serialized to NBT");
        return nbt;
    }

    public static FrameBody deserializeFromNBT(NBTCompound nbt) {
        CoreComponentLogger.info(CoreComponentType.FRAME_BODY, "Deserializing FrameBody from NBT");

        Grade grade = Grade.valueOf(nbt.getString("grade"));
        Rarity rarity = Rarity.valueOf(nbt.getString("rarity"));
        Tier tier = Tier.valueOf(nbt.getString("tier"));

        FrameBody frameBody = new FrameBody(grade, rarity, tier);
        frameBody.setLifespan(nbt.getInteger("lifespan"));
        frameBody.setMaxFrameAttachments(nbt.getInteger("maxFrameAttachments"));

        NBTCompound componentsNBT = nbt.getCompound("components");
        for (String key : componentsNBT.getKeys()) {
            CoreComponentType type = CoreComponentType.valueOf(key);
            CoreComponent component = deserializeComponent(type, componentsNBT.getCompound(key));
            if (component != null) {
                frameBody.addComponent(component);
            }
        }

        NBTCompound attachmentsNBT = nbt.getCompound("frameAttachments");
        for (String key : attachmentsNBT.getKeys()) {
            FrameAttachment attachment = deserializeFrameAttachment(attachmentsNBT.getCompound(key));
            if (attachment != null) {
                frameBody.addFrameAttachment(attachment);
            }
        }

        CoreComponentLogger.info(CoreComponentType.FRAME_BODY, "FrameBody deserialized from NBT");
        return frameBody;
    }

    private static CoreComponent deserializeComponent(CoreComponentType type, NBTCompound nbt) {
        switch (type) {
            case CHARGE_CELL:
                return ChargeCell.deserializeFromNBT(nbt);
            case ENERGY_CORE:
                return EnergyCore.deserializeFromNBT(nbt);
            case CORE_PROCESSOR:
                return CoreProcessor.deserializeFromNBT(nbt);
            case LENS_CONDUIT:
                return LensConduit.deserializeFromNBT(nbt);
            default:
                CoreComponentLogger.warning(CoreComponentType.FRAME_BODY, "Unknown component type during deserialization: " + type);
                return null;
        }
    }

    private static FrameAttachment deserializeFrameAttachment(NBTCompound nbt) {
        // Implement this method based on your FrameAttachment structure
        // This is a placeholder implementation
        CoreComponentLogger.warning(CoreComponentType.FRAME_BODY, "FrameAttachment deserialization not implemented");
        return null;
    }

    public ItemStack createItemStack() {
        logDebug("Creating ItemStack for FrameBody");
        ItemStack item = new ItemStack(Material.NETHERITE_HOE); // You can change this to whatever material represents your FrameBody
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Frame Body");
        meta.setLore(getFrameBodyLore());
        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.mergeCompound(this.serializeToNBT());
        });

        logInfo("Created ItemStack for FrameBody: " + frameUUID);
        return item;
    }

    public static FrameBody fromItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            CoreComponentLogger.warning(CoreComponentType.FRAME_BODY, "Invalid ItemStack for FrameBody creation");
            return null;
        }

        return NBT.get(item, (nbt) -> {
            if (nbt.hasTag("uuid")) {
                return deserializeFromNBT((NBTCompound) nbt);
            } else {
                CoreComponentLogger.warning(CoreComponentType.FRAME_BODY, "ItemStack does not contain FrameBody NBT data");
                return null;
            }
        });
    }

    @Override
    protected void logInfo(String message) {
        CoreComponentLogger.info(CoreComponentType.FRAME_BODY, "[" + frameUUID + "] " + message);
    }

    @Override
    protected void logWarning(String message) {
        CoreComponentLogger.warning(CoreComponentType.FRAME_BODY, "[" + frameUUID + "] " + message);
    }

    @Override
    protected void logSevere(String message) {
        CoreComponentLogger.severe(CoreComponentType.FRAME_BODY, "[" + frameUUID + "] " + message);
    }

    @Override
    protected void logDebug(String message) {
        CoreComponentLogger.debug(CoreComponentType.FRAME_BODY, "[" + frameUUID + "] " + message);
    }


//    public List<String> generateDebugLore(FrameBody weaponData) {
//        List<String> lore = new ArrayList<>();
//        if (weaponData.getEnergyCore() != null) {
//            EnergyCore ec = weaponData.getEnergyCore();
//            lore.add(ChatColor.DARK_GRAY + "--EnergyCore--");
//            lore.add(ChatColor.GRAY + "UUID: " + ec.getUuid());
//            lore.add(ChatColor.GRAY + "Tier: " + ec.getTier().getTierFormat());
//            lore.add(ChatColor.GRAY + "Grade: " + ec.getGrade().getGradeFormat());
//            lore.add(ChatColor.GRAY + "Rarity: " + ec.getRarity().getRarityFormat());
//            lore.add(ChatColor.GRAY + "OutputRate: " + ec.getOutputEnergyRate());
//            lore.add(ChatColor.GRAY + "CoreCapacitance: " + ec.getCoreEnergyCapacitance());
//            lore.add(ChatColor.GRAY + "RechargeRate: " + ec.getRechargeRate());
//            lore.add(ChatColor.GRAY + "HeatRate: " + ec.getHeatRate());
//            lore.add(ChatColor.GRAY + "DrawRate: " + ec.getIdleDrawRate());
//            if (ec.getComponentUpgrades() != null && !ec.getComponentUpgrades().isEmpty()) {
//                lore.add(ChatColor.GRAY+"--ComponentUpgrades--");
//                for (ComponentUpgrade<?> comp : ec.getComponentUpgrades()) {
//                    lore.add(ChatColor.GRAY+"["+comp.getComponentUpgradeType().name()+"] " + comp.getTier().getTierFormat() + " " + comp.getGrade().getGradeFormat() + comp.getRarity().getRarityFormat());
//                    if (comp.getAppliedTo() != null) { // Add this null check
//                        lore.add(ChatColor.GRAY + "AppliedTo: " + comp.getAppliedTo().toString());
//                    } else {
//                        lore.add(ChatColor.GRAY + "AppliedTo: None");
//                    }
//                    lore.add(ChatColor.GRAY+"Enables: //need switch statement to check each");
//                }
//            }
//            lore.add(ChatColor.GRAY + "Lifespan:" + ec.getLifespan());
//            lore.add(ChatColor.DARK_GRAY + "-----------------");
//        }
//        if (weaponData.getChargeCell() != null) {
//            ChargeCell cc = weaponData.getChargeCell();
//            lore.add(ChatColor.DARK_GRAY + "--ChargeCell--");
//            lore.add(ChatColor.GRAY + "UUID: " + cc.getUuid());
//            lore.add(ChatColor.GRAY + "Tier: " + cc.getTier().getTierFormat());
//            lore.add(ChatColor.GRAY + "Grade: " + cc.getGrade().getGradeFormat());
//            lore.add(ChatColor.GRAY + "Rarity: " + cc.getRarity().getRarityFormat());
//            lore.add(ChatColor.GRAY + "OutputRate: " + cc.getCurrentOutputRate());
//            lore.add(ChatColor.GRAY + "capacity: " + cc.getCapacity());
//            lore.add(ChatColor.GRAY + "RechargeRate: " + cc.getCurrentChargeRate());
//            lore.add(ChatColor.GRAY + "HeatRate: " + cc.getHeatRate());
//            lore.add(ChatColor.GRAY + "upgradelimit: " + cc.getUpgradeLimit());
//            if (cc.getComponentUpgrades() != null && !cc.getComponentUpgrades().isEmpty()) {
//                lore.add(ChatColor.GRAY+"--ComponentUpgrades--");
//                for (ComponentUpgrade<?> comp : cc.getComponentUpgrades()) {
//                    lore.add(ChatColor.GRAY+"["+comp.getComponentUpgradeType().name()+"] " + comp.getTier().getTierFormat() + " " + comp.getGrade().getGradeFormat() + comp.getRarity().getRarityFormat());
//                    if (comp.getAppliedTo() != null) { // Add this null check
//                        lore.add(ChatColor.GRAY + "AppliedTo: " + comp.getAppliedTo().toString());
//                    } else {
//                        lore.add(ChatColor.GRAY + "AppliedTo: None");
//                    }
//                    lore.add(ChatColor.GRAY+"Enables: //need switch statement to check each");
//                }
//            }
//            lore.add(ChatColor.GRAY + "Lifespan:" + cc.getLifespan());
//            lore.add(ChatColor.DARK_GRAY + "-----------------");
//        }
//        if (weaponData.getCoreProcessor() != null) {
//            CoreProcessor cp = weaponData.getCoreProcessor();
//            lore.add(ChatColor.DARK_GRAY + "--Core Processor--");
//            lore.add(ChatColor.GRAY + "UUID: " + cp.getUuid());
//            lore.add(ChatColor.GRAY + "Tier: " + cp.getTier().getTierFormat());
//            lore.add(ChatColor.GRAY + "Grade: " + cp.getGrade().getGradeFormat());
//            lore.add(ChatColor.GRAY + "Rarity: " + cp.getRarity().getRarityFormat());
//            lore.add(ChatColor.GRAY + "Ghz: " + cp.getGigaHertz());
//            lore.add(ChatColor.GRAY + "GhzReqLimit: " + cp.getGigaHertzReq());
//            lore.add(ChatColor.GRAY + "IdleDrawRate: " + cp.getIdleDrawRate());
//            lore.add(ChatColor.GRAY + "HeatRate: " + cp.getHeatRate());
//            lore.add(ChatColor.GRAY + "upgradelimit: " + cp.getUpgradeLimit());
//            if (cp.getComponentUpgrades() != null && !cp.getComponentUpgrades().isEmpty()) {
//                lore.add(ChatColor.GRAY+"--ComponentUpgrades--");
//                for (ComponentUpgrade<?> comp : cp.getComponentUpgrades()) {
//                    lore.add(ChatColor.GRAY+"["+comp.getComponentUpgradeType().name()+"] " + comp.getTier().getTierFormat() + " " + comp.getGrade().getGradeFormat() + comp.getRarity().getRarityFormat());
//                    if (comp.getAppliedTo() != null) { // Add this null check
//                        lore.add(ChatColor.GRAY + "AppliedTo: " + comp.getAppliedTo().toString());
//                    } else {
//                        lore.add(ChatColor.GRAY + "AppliedTo: None");
//                    }
//                    lore.add(ChatColor.GRAY+"Enables: //need switch statement to check each");
//                }
//            }
//            lore.add(ChatColor.GRAY + "Lifespan:" + cp.getLifespan());
//            lore.add(ChatColor.DARK_GRAY + "-----------------");
//        }
//        if (weaponData.getLensConduit() != null) {
//            LensConduit cp = weaponData.getLensConduit();
//            lore.add(ChatColor.DARK_GRAY + "--Core Processor--");
//            lore.add(ChatColor.GRAY + "UUID: " + cp.getUuid());
//            lore.add(ChatColor.GRAY + "Tier: " + cp.getTier().getTierFormat());
//            lore.add(ChatColor.GRAY + "Grade: " + cp.getGrade().getGradeFormat());
//            lore.add(ChatColor.GRAY + "Rarity: " + cp.getRarity().getRarityFormat());
//            lore.add(ChatColor.GRAY + "CurrentLens: " + cp.getCurrentLense());
//            lore.add(ChatColor.GRAY + "LensState: " + cp.getLenseState());
//            lore.add(ChatColor.GRAY + "LensType: " + cp.getLenseType());
//            lore.add(ChatColor.GRAY + "FiringRate: " + cp.getFiringRate());
//            lore.add(ChatColor.GRAY + "Beam: " + cp.isBeamType());
//            lore.add(ChatColor.GRAY + "upgradelimit: " + cp.getUpgradeLimit());
//            if (cp.getFocalUpgrades() != null && !cp.getFocalUpgrades().isEmpty()) {
//                lore.add(ChatColor.GRAY+"--ComponentUpgrades--");
//                for (FocalUpgrade<?> comp : cp.getFocalUpgrades()) {
//                    lore.add(ChatColor.GRAY+"["+comp.getComponentUpgradeType().name()+"] " + comp.getTier().getTierFormat() + " " + comp.getGrade().getGradeFormat() + comp.getRarity().getRarityFormat());
//                    if (comp.getAppliedTo() != null) { // Add this null check
//                        lore.add(ChatColor.GRAY + "AppliedTo: " + comp.getAppliedTo().toString());
//                    } else {
//                        lore.add(ChatColor.GRAY + "AppliedTo: None");
//                    }
//                    lore.add(ChatColor.GRAY+"Enables: //need switch statement to check each");
//                }
//            }
//            lore.add(ChatColor.GRAY + "Lifespan:" + cp.getLifespan());
//            lore.add(ChatColor.DARK_GRAY + "-----------------");
//        }
//        lore.add(ChatColor.GRAY+"Attachments: " + (weaponData.getFrameAttachments().isEmpty() ? 0 : weaponData.getFrameAttachments().size()) + "/" + weaponData.getFrameBody().getMaxFrameAttachments());
//        lore.add(ChatColor.GRAY+"Lifespan:" + weaponData.getFrameBody().getLifespan());
//        lore.add(ChatColor.GRAY+"UUID:" + weaponData.getFrameBody().getFrameUUID());
//        lore.add(ChatColor.GRAY + "Tier: " + weaponData.getTier().getTierFormat());
//        lore.add(ChatColor.GRAY + "Rarity: " + weaponData.getRarity().getRarityFormat());
//        lore.add(ChatColor.GRAY + "Grade: " + weaponData.getGrade().getGradeFormat());
//        return lore;
//    }

//    public ItemStack set(ItemStack itemStack, FrameBody weaponData, Player player) {
//        ItemMeta itemMeta = itemStack.getItemMeta();
//        NBT.modify(itemStack, readWriteItemNBT -> {
//            readWriteItemNBT.clearNBT();
//            readWriteItemNBT.setString("uuid", weaponData.getFrameUUID().toString());
//            readWriteItemNBT.setString("grade", weaponData.getGrade().getGradeFormat());
//            readWriteItemNBT.setString("rarity", weaponData.getRarity().getRarityFormat());
//            readWriteItemNBT.setString("tier", weaponData.getTier().getTierFormat());
//            readWriteItemNBT.setInteger("lifespan", weaponData.getLifespan());
////            readWriteItemNBT.set("components", weaponData.chargeCell);
//            readWriteItemNBT.modifyMeta((readableNBT, itemMeta1) -> {
//                itemMeta1.setDisplayName(weaponData.getGrade().getGradeFormat() + " FrameBody");
//
//                itemMeta1.setLore(generateDebugLore(weaponData);
//                itemMeta1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
//            });
//            readWriteItemNBT.getOrCreateCompound("test");
//        });
//
//        player.getInventory().addItem(itemStack);
//        itemStack.setItemMeta(itemMeta);
//        return itemStack;
//    }

    public static String getFrameBodyUUID(ItemStack itemStack) {
        return NBT.get(itemStack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("uuid"));
    }

//    public ItemStack updateLoreNBT(ItemStack itemstack, FrameBody weaponData) {
//        NBT.modify(itemstack, readWriteItemNBT -> {
//            readWriteItemNBT.setString("uuid", weaponData.getFrameUUID().toString());
//            readWriteItemNBT.setInteger("lifespan", weaponData.getLifespan());
//            readWriteItemNBT.modifyMeta((readableNBT, itemMeta1) -> {
//                itemMeta1.setDisplayName(weaponData.getFrameBody().getGrade().getGradeFormat() + " FrameBody");
//                List<String> lore = new ArrayList<>();
//                if (weaponData.getEnergyCore() != null) {
//                    EnergyCore ec = weaponData.getEnergyCore();
//                    lore.add(ChatColor.GRAY+"--EnergyCore--");
//                    lore.add(ChatColor.GRAY+"UUID: " + ec.getUuid());
//                    lore.add(ChatColor.GRAY + "Tier: " + ec.getTier().getTierFormat());
//                    lore.add(ChatColor.GRAY + "Grade: " + ec.getGrade().getGradeFormat());
//                    lore.add(ChatColor.GRAY+"Rarity: " + ec.getRarity().getRarityFormat());
//                    lore.add(ChatColor.GRAY + "OutputRate: " + ec.getOutputEnergyRate());
//                    lore.add(ChatColor.GRAY + "CoreCapacitance: " + ec.getCoreEnergyCapacitance());
//                    lore.add(ChatColor.GRAY + "RechargeRate: " + ec.getRechargeRate());
//                    lore.add(ChatColor.GRAY + "HeatRate: " + ec.getHeatRate());
//                    lore.add(ChatColor.GRAY+ "drawrate: " + ec.getIdleDrawRate());
//                    if (ec.getComponentUpgrades() != null && !ec.getComponentUpgrades().isEmpty()) {
//                        lore.add(ChatColor.GRAY+"--ComponentUpgrades--");
//                        for (ComponentUpgrade<?> comp : ec.getComponentUpgrades()) {
//                            lore.add(ChatColor.GRAY+"["+comp.getComponentUpgradeType().name()+"] " + comp.getTier().getTierFormat() + " " + comp.getGrade().getGradeFormat() + comp.getRarity().getRarityFormat());
//                            if (comp.getAppliedTo() != null) { // Add this null check
//                                lore.add(ChatColor.GRAY + "AppliedTo: " + comp.getAppliedTo().toString());
//                            } else {
//                                lore.add(ChatColor.GRAY + "AppliedTo: None");
//                            }
//                            lore.add(ChatColor.GRAY+"Enables: //need switch statement to check each");
//                        }
//                    }
//                    lore.add(ChatColor.GRAY + "Lifespan:" + ec.getLifespan());
//                    lore.add(ChatColor.DARK_GRAY + "-----------------");
//                }
//                if (weaponData.getChargeCell() != null) {
//                    ChargeCell ec = weaponData.getChargeCell();
//                    lore.add(ChatColor.GRAY+"--ChargeCell--");
//                    lore.add(ChatColor.GRAY+"UUID: " + ec.getUuid());
//                    lore.add(ChatColor.GRAY + "Tier: " + ec.getTier().getTierFormat());
//                    lore.add(ChatColor.GRAY + "Grade: " + ec.getGrade().getGradeFormat());
//                    lore.add(ChatColor.GRAY+"Rarity: " + ec.getRarity().getRarityFormat());
//                    lore.add(ChatColor.GRAY + "OutputRate: " + ec.getCurrentOutputRate());
//                    lore.add(ChatColor.GRAY + "capacity: " + ec.getCapacity());
//                    lore.add(ChatColor.GRAY + "RechargeRate: " + ec.getCurrentChargeRate());
//                    lore.add(ChatColor.GRAY + "HeatRate: " + ec.getHeatRate());
//                    lore.add(ChatColor.GRAY+"upgradelimit: " + ec.getUpgradeLimit());
//                    if (ec.getComponentUpgrades() != null && !ec.getComponentUpgrades().isEmpty()) {
//                        lore.add(ChatColor.GRAY+"--ComponentUpgrades--");
//                        for (ComponentUpgrade<?> comp : ec.getComponentUpgrades()) {
//                            lore.add(ChatColor.GRAY+"["+comp.getComponentUpgradeType().name()+"] " + comp.getTier().getTierFormat() + " " + comp.getGrade().getGradeFormat() + comp.getRarity().getRarityFormat());
//                            if (comp.getAppliedTo() != null) { // Add this null check
//                                lore.add(ChatColor.GRAY + "AppliedTo: " + comp.getAppliedTo().toString());
//                            } else {
//                                lore.add(ChatColor.GRAY + "AppliedTo: None");
//                            }
//                            lore.add(ChatColor.GRAY+"Enables: //need switch statement to check each");
//                        }
//                    }
//                    lore.add(ChatColor.GRAY + "Lifespan:" + ec.getLifespan());
//                    lore.add(ChatColor.DARK_GRAY + "-----------------");
//                }
//
//
//                lore.add(ChatColor.GRAY+"Attachments: " + (weaponData.getFrameAttachments().isEmpty() ? 0 : weaponData.getFrameAttachments().size()) + "/" + weaponData.getFrameBody().getMaxFrameAttachments());
//                lore.add(ChatColor.GRAY+"Lifespan:" + weaponData.getFrameBody().getLifespan());
//                lore.add(ChatColor.GRAY+"UUID:" + weaponData.getFrameBody().getFrameUUID());
//                itemMeta1.setLore(lore);
//                itemMeta1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
//            });
//            readWriteItemNBT.getOrCreateCompound("test");
//        });
//
//        return itemstack;
//    }

//
//
//    public boolean addUpgradeToChargeCell(ComponentUpgrade componentUpgrade) {
//        //todo maybe check if chargecell is correct one.
//        if (!(isApplyingCorrectUpgrade(chargeCell, componentUpgrade))) {
//            return false;
//        }
//        if (!canAddSuperUpgrade(componentUpgrade)) {
//            return false;
//        }
//        if (chargeCell.hasHitUpgradeLimit()){
//            return false;
//        }
//        chargeCell.addUpgrade(componentUpgrade);
//        return true;
//    }
//
//
//    public boolean isApplyingCorrectCoreComponent(CoreComponent coreComponent) {
//
//
//        return true;
//    }
//
//
//
//
//
//
//    public boolean isApplyingCorrectUpgrade(CoreComponent coreComponent, ComponentUpgrade componentUpgrade) {
//        var componentUpgradeType = componentUpgrade.getComponentUpgradeType();
//        if (!(componentUpgradeType.getCoreComponentType() == coreComponent.getComponentType())) {
//            System.out.println("You may only apply " + componentUpgradeType.getCoreComponentType().toString().toUpperCase()
//                    + " component upgrade on " + coreComponent.getComponentType().toString().toUpperCase() + "'s");
//            return false;
//        }
//        return true;
//    }
//
//    public boolean canAddSuperUpgrade(ComponentUpgrade componentUpgrade) {
//        if (isSuperUpgrade(componentUpgrade.getComponentUpgradeType()) && !chargeCellHasOverCharge()) {
//            System.out.println("You need OverCharge upgrade in ChargeCell to add " + componentUpgrade.getComponentUpgradeType());
//            return false;
//        }
//        // You can add more conditions here if you need to check other dependencies for other upgrades
//        return true;
//    }
//
//    private boolean isSuperUpgrade(ComponentUpgradeType upgradeType) {
//        return upgradeType == ComponentUpgradeType.SUPERCLOCK ||
//                upgradeType == ComponentUpgradeType.SUPERVOLT ||
//                upgradeType == ComponentUpgradeType.SUPERLOAD;
//    }
//
//    private boolean chargeCellHasOverCharge() {
//        return chargeCell.getUpgrades().stream().anyMatch(upgrade -> upgrade.getComponentUpgradeType() == ComponentUpgradeType.OVERCHARGE);
//    }
//
//    public boolean addUpgradeToEnergyCore(ComponentUpgrade componentUpgrade) {
//        if (!isApplyingCorrectUpgrade(energyCore, componentUpgrade)){
//            return false;
//        }
//        if (!canAddSuperUpgrade(componentUpgrade)) {
//            return false;
//        }
//        if (chargeCell.hasHitUpgradeLimit()){
//            return false;
//        }
//        energyCore.addUpgrade(componentUpgrade);
//        return true;
//    }

//    public void debug() {
//        System.out.println("=== FrameBody Debug Information ===");
//        System.out.println("UUID: " + frameBody.getFrameUUID());
//        System.out.println("Grade: " + frameBody.getGrade().getGradeFormat());
//        System.out.println("Lifespan: " + frameBody.getLifespan());
//        System.out.println("Max Frame Attachments: " + frameBody.getMaxFrameAttachments());
//
//        // Debugging ChargeCell
//        System.out.println("\n--- ChargeCell ---");
//        if (chargeCell != null) {
//            System.out.println("FrameUUID: " + chargeCell.getFrameBodyParent().getFrameUUID());
//            System.out.println("Lifespan: " + chargeCell.getLifespan());
//            System.out.println("Current Capacity: " + chargeCell.getCapacity());
//            System.out.println("Current Charge Rate: " + chargeCell.getCurrentChargeRate());
//            System.out.println("Current Output Rate: " + chargeCell.getCurrentOutputRate());
//            System.out.println("Heat Rate: " + chargeCell.getHeatRate());
//            System.out.println("--- Component Upgrades ---");
//            for (ComponentUpgrade<?> upgrade : chargeCell.getUpgrades()) {
//                System.out.println("framebodyParent: " + ((ChargeCell)upgrade.getAppliedTo()).getFrameBodyParent().getFrameUUID());
//                System.out.println(upgrade.getClass().getSimpleName() + " (Tier: " + upgrade.getTier() + ", Rarity: " + upgrade.getRarity() + ", Grade: " + upgrade.getGrade() + ")");
//            }
//        } else {
//            System.out.println("No ChargeCell attached.");
//        }
//
//        // Debugging EnergyCore (Just an example. You can expand upon this)
//        System.out.println("\n--- EnergyCore ---");
//        if (energyCore != null) {
//            // Add properties of EnergyCore that you want to debug here.
//        } else {
//            System.out.println("No EnergyCore attached.");
//        }
//
//        // Debugging CoreProcessor (Just an example. You can expand upon this)
//        System.out.println("\n--- CoreProcessor ---");
//        if (coreProcessor != null) {
//            // Add properties of CoreProcessor that you want to debug here.
//        } else {
//            System.out.println("No CoreProcessor attached.");
//        }
//
//        // Debugging LenseConduit (Just an example. You can expand upon this)
//        System.out.println("\n--- LenseConduit ---");
//        if (lensConduit != null) {
//            // Add properties of LenseConduit that you want to debug here.
//        } else {
//            System.out.println("No LenseConduit attached.");
//        }
//
//        // Debugging Frame Attachments
//        System.out.println("\n--- Frame Attachments ---");
//        if (frameBody.getFrameAttachments() != null && !frameBody.getFrameAttachments().isEmpty()) {
//            for (FrameAttachment attachment : frameBody.getFrameAttachments()) {
//                System.out.println(attachment.getClass().getSimpleName());
//            }
//        } else {
//            System.out.println("No Frame Attachments.");
//        }
//
//        System.out.println("===================================");
//    }

//    public NBTCompound serializeToNBT() {
////        String uuid = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("uuid"));
//        NBTCompound nbt = (NBTCompound) NBT.createNBTObject();
//        nbt.getOrCreateCompound("frameBody");
//        nbt.setString("uuid", frameBody.getFrameUUID().toString() != null ? frameBody.getFrameUUID().toString() : UUID.randomUUID().toString());
//        nbt.setInteger("lifespan", (Integer) frameBody.getLifespan());
//
//        // For enums and other complex attributes, you can further serialize them.
//        nbt.setInteger("grade", (Integer) grade.getGradeLadder());
//
//        // Serialize componentUpgrades (assuming you have a way to serialize/deserialize each ComponentUpgrade)
//        NBTCompoundList nbts = nbt.getCompoundList("componentUpgrades");
//        componentUpgrades.forEach(componentUpgrade -> nbts.addCompound().mergeCompound(componentUpgrade.serialize()));
//        nbt.mergeCompound((ReadWriteNBT) nbts);
//
//        return nbt;
//    }
//
//
//    /**
//     * Deserialize an EnergyCore from an NBTCompound.
//     *
//     * @param nbt the source NBTCompound.
//     * @return a new EnergyCore constructed from the given NBT data.
//     */
//    public FrameBody deserializeFromNBT(NBTCompound nbt) {
//        Grade grade = Grade.valueOf(nbt.getString("grade"));
//        Rarity rarity = Rarity.valueOf(nbt.getString("rarity"));
//        Tier tier = Tier.valueOf(nbt.getString("tier"));
//
//        FrameBody framebody = new FrameBody(grade, rarity, tier);
//        framebody.setFrameUUID(UUID.fromString(nbt.getString("uuid")));
//        if (framebody.getEnergyCore() != null) {
//            EnergyCore ec = frameBody.getEnergyCore();
//            framebody.setEnergyCore(ec);
//
//        }
//
//        // Deserialize componentUpgrades
//        ReadWriteNBTCompoundList upgrades = nbt.getCompoundList("componentUpgrades");
//        upgrades.forEach(readWriteNBT -> framebody.componentUpgrades.add(ComponentUpgrade.deserialize(readWriteNBT)));
//        for (ReadWriteNBT upgradeNBT : upgrades) {
//            ComponentUpgrade<?> upgrade = ComponentUpgrade.deserialize(upgradeNBT); // Assumes ComponentUpgrade has a static deserialize method
//            framebody.componentUpgrades.add(upgrade);
//        }
//
//        return framebody;
//    }


//    public boolean addUpgradeToCoreProcessor(ComponentUpgrade componentUpgrade) {
//        if (!canAddUpgradeToCore(componentUpgrade)) {
//            return false;
//        }
//
//        coreProcessor.addUpgrade(componentUpgrade);
//        return true;
//    }

//    public static class NBTHandler implements de.tr7zw.changeme.nbtapi.iface.NBTHandler {
//
//        // Keys for FrameBody's NBT data
//        private static final String FRAME_UUID_KEY = "frameUUID";
//        private static final String LIFESPAN_KEY = "lifespan";
//        private static final String ENERGY_CORE_KEY = "energyCore";  // This is for nested EnergyCore data
//
//        public void setNBTData(ItemStack itemStack, FrameBody frameBody) {
//            NBT.modify(itemStack, nbt -> {
//                nbt.setUUID(FRAME_UUID_KEY, frameBody.getFrameUUID());
//                nbt.setInteger(LIFESPAN_KEY, frameBody.getLifespan());
//
//                // Nested EnergyCore data
//                ReadWriteNBT energyCoreNBT = EnergyCore.NBTHandler.serializeToNBT(frameBody.getEnergyCore());
//                nbt.set(ENERGY_CORE_KEY, energyCoreNBT, this);
//            });
//        }
//
//        public FrameBody getNBTData(ItemStack itemStack) {
//            FrameBody frameBody = new FrameBody();
//
//            UUID frameUUID = NBT.get(itemStack, (Function<ReadableItemNBT, UUID>) nbt -> nbt.getUUID(FRAME_UUID_KEY));
//            int lifespan = NBT.get(itemStack, (Function<ReadableItemNBT, Integer>) nbt -> nbt.getOrDefault(LIFESPAN_KEY, 0));
//
//            // Get nested EnergyCore data
//            ReadWriteNBT energyCoreNBT = NBT.get(itemStack, (Function<ReadableItemNBT, ReadWriteNBT>) nbt -> nbt.get(ENERGY_CORE_KEY));
//            EnergyCore energyCore = EnergyCore.NBTHandler.deserializeFromNBT(energyCoreNBT);
//
//            frameBody.setFrameUUID(frameUUID);
//            frameBody.setLifespan(lifespan);
//            frameBody.setEnergyCore(energyCore);
//
//            return frameBody;
//        }
//
//        @Override
//        public void set(@NotNull ReadWriteNBT nbt, @NotNull String key, @NotNull Object value) {
//
//        }
//
//        @Override
//        public Object get(@NotNull ReadableNBT nbt, @NotNull String key) {
//            return null;
//        }
//    }

//    public final static class FrameBodyHandler implements NBTHandler<FrameBody> {
//
//        @Override
//        public void set(@NotNull ReadWriteNBT nbt, @NotNull String key, @NotNull FrameBody value) {
//            nbt.removeKey(key);
//            nbt.getOrCreateCompound(key).mergeCompound(value.serializeToNBT());
//        }
//
//        @Override
//        public FrameBody get(@NotNull ReadableNBT nbt, @NotNull String key) {
//            if (nbt.hasTag(key)) { // Check if the key exists
//                ReadableNBT frameBodyNBT = nbt.getCompound(key); // Getting the compound associated with the key
//                FrameBody fb = new FrameBody();
//                assert frameBodyNBT != null;
//                fb.deserializeFromNBT(frameBodyNBT); // Assuming FrameBody has a method to populate itself from NBT
//                return fb;
//            }
//            return null; // Or return a new default FrameBody, based on your requirements
//        }
//    }
}
