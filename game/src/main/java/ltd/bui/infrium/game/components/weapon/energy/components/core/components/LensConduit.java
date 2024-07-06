package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.Lense;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.LenseState;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.LenseType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.attachments.FocalUpgrade;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.function.Consumer;

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


    @Override
    public NBTCompound serializeToNBT() {
        return null;
    }

    @Override
    public CoreComponent deserializeFromNBT(NBTCompound nbt) {
        return null;
    }
}
