package ltd.bui.infrium.game.components.weapon.energy.components.core;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.FastCharge;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CoreComponent implements ICoreComponent {
    @Getter @Setter
    protected Rarity rarity;
    @Getter @Setter
    protected Grade grade;
    @Getter @Setter
    protected Tier tier;

    @Getter @Setter
    protected CoreComponentType componentType;

    @Getter @Setter
    protected Set<ComponentUpgrade<? extends CoreComponent>> componentUpgrades;

    public Collection<ComponentUpgrade<? extends CoreComponent>> getUpgrades() {
        return componentUpgrades;
    }


    public CoreComponent(Rarity rarity, Grade grade, Tier tier, CoreComponentType componentType) {
        this.rarity = rarity;
        this.grade = grade;
        this.tier = tier;
        this.componentType = componentType;
        this.componentUpgrades = new HashSet<>();
    }

    protected void logInfo(String message) {
        CoreComponentLogger.info(componentType, message);
    }

    protected void logWarning(String message) {
        CoreComponentLogger.warning(componentType, message);
    }

    protected void logSevere(String message) {
        CoreComponentLogger.severe(componentType, message);
    }

    protected void logDebug(String message) {
        CoreComponentLogger.debug(componentType, message);
    }



}
