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

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class LensConduit extends CoreComponent {

    @Getter @Setter private FrameBody frameBodyParent;
    @Getter @Setter private UUID uuid;
    @Getter @Setter private double lifespan; // lifespan of chargecell(total chargeRate throughput x total outputRate + capacity) //TODO

    @Getter @Setter private Lense currentLense;
    @Getter @Setter private LenseState lenseState;
    @Getter @Setter private LenseType lenseType;
    @Getter @Setter private int firingRate;
    @Getter @Setter private boolean beamType;


    @Getter @Setter
    private Set<FocalUpgrade<?>> focalUpgrades; // Lense, LifespanScatter Lenses, Modulated Focus (all affect lifespan,chargeRate,outputRate and heatRate)
    @Getter @Setter private Integer upgradeLimit;

    public LensConduit(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.LENS_CONDUIT);
    }

}
