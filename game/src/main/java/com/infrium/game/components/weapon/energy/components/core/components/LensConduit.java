package com.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.Getter;
import lombok.Setter;
import com.infrium.game.components.weapon.energy.components.core.CoreComponent;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentLogger;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.Lense;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.LenseState;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.LenseType;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.attachments.FocalUpgrade;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter
public class LensConduit extends CoreComponent {

    @Setter private FrameBody frameBodyParent;
    @Setter private LensConduit lensConduit;
    @Setter private UUID uuid;
    @Setter private double lifespan; // lifespan of chargecell(total chargeRate throughput x total outputRate + capacity) //TODO

    @Setter private Lense currentLense;
    @Setter private LenseState lenseState;
    @Setter private LenseType lenseType;
    @Setter private int firingRate;
    @Setter private boolean beamType;


    @Setter
    private Set<FocalUpgrade<?>> focalUpgrades; // Lense, LifespanScatter Lenses, Modulated Focus (all affect lifespan,chargeRate,outputRate and heatRate)
    @Setter private Integer upgradeLimit;

    public LensConduit(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.LENS_CONDUIT);
        lifespan = grade.getLifespan();
        upgradeLimit = rarity.getComponentUpgradeLimit();

    }

    public LensConduit(FrameBody frameBodyParent, Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.LENS_CONDUIT);
        lensConduit = this;
        this.frameBodyParent = frameBodyParent;
    }

    public void setLens(Lense lense) {
        this.currentLense = lense;
    }

    public void setLensState(LenseState lenseState) {
        this.lenseState = lenseState;
    }

    public List<String> getLensConduitLore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Lens Conduit");
        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GRAY + "UUID: " + ChatColor.WHITE + lensConduit.getUuid());
        lore.add(ChatColor.GRAY + "Tier: " + ChatColor.WHITE + lensConduit.getTier());
        lore.add(ChatColor.GRAY + "Grade: " + ChatColor.WHITE + lensConduit.getGrade());
        lore.add(ChatColor.GRAY + "Rarity: " + ChatColor.WHITE + lensConduit.getRarity());
        lore.add(ChatColor.GRAY + "Lifespan: " + ChatColor.WHITE + lensConduit.getLifespan() + "s");
        lore.add(ChatColor.GRAY + "Lense Type: " + ChatColor.WHITE + lensConduit.getLenseType());
        lore.add(ChatColor.GRAY + "Lense State: " + ChatColor.WHITE + lensConduit.getLenseState());
        lore.add(ChatColor.GRAY + "Firing Rate: " + ChatColor.WHITE + lensConduit.getFiringRate() + " shots/s");
        lore.add(ChatColor.GRAY + "Beam Type: " + ChatColor.WHITE + (lensConduit.isBeamType() ? "Beam" : "Pulse"));
        lore.add(ChatColor.GRAY + "Upgrades: " + (lensConduit.getFocalUpgrades() != null ? "[" + lensConduit.getFocalUpgrades().size() + "/" + lensConduit.getUpgradeLimit() + "] (click for upgrades)" : "[0/0] (click for upgrades)"));
        if (lensConduit.getFocalUpgrades() != null) {
            lensConduit.getFocalUpgrades().forEach(upgrade -> lore.add(ChatColor.GRAY + "  - " + upgrade.getComponentUpgradeType().name()));
        }
        lore.add(ChatColor.GRAY + "---------------------");

        return lore;
    }



    public void addUpgrade(FocalUpgrade upgrade) {
            if (focalUpgrades == null) {
                focalUpgrades = new HashSet<>();
            }
            if (focalUpgrades.size() < upgradeLimit) {
                focalUpgrades.add((FocalUpgrade<?>) upgrade);
            }

    }

    @Override
    public void addUpgrade(ComponentUpgrade<? extends CoreComponent> upgrade) {

    }

    @Override
    public void onTick() {
        if (lensConduit.getFocalUpgrades() != null) {
//            lensConduit.getFocalUpgrades().forEach(focalUpgrade -> focalUpgrade.onTick());
        }
    }

    @Override
    public NBTCompound serializeToNBT() {
        return null;
    }

    public static LensConduit deserializeFromNBT(NBTCompound nbt) {
        return null;
    }

    public ItemStack createItemStack() {
        logDebug("Creating ItemStack for FrameBody");
        ItemStack item = new ItemStack(Material.NETHERITE_HOE); // You can change this to whatever material represents your FrameBody
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Frame Body");
        meta.setLore(getLensConduitLore());
        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.mergeCompound(this.serializeToNBT());
        });

        logInfo("Created ItemStack for FrameBody: " + uuid);
        return item;
    }

    public static LensConduit fromItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            CoreComponentLogger.warning(CoreComponentType.LENS_CONDUIT, "Invalid ItemStack for FrameBody creation");
            return null;
        }

        return NBT.get(item, (nbt) -> {
            if (nbt.hasTag("uuid")) {
                return deserializeFromNBT((NBTCompound) nbt);
            } else {
                CoreComponentLogger.warning(CoreComponentType.LENS_CONDUIT, "ItemStack does not contain FrameBody NBT data");
                return null;
            }
        });
    }
}
