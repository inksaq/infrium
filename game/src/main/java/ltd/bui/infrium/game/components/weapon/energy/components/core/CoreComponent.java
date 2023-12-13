package ltd.bui.infrium.game.components.weapon.energy.components.core;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
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

    protected Set<ComponentUpgrade<? extends CoreComponent>> componentUpgrades = new HashSet<>();



    public Collection<ComponentUpgrade<? extends CoreComponent>> getUpgrades() {
        return componentUpgrades;
    }



    public CoreComponent(Rarity rarity, Grade grade, Tier tier, CoreComponentType componentType) {
        this.rarity = rarity;
        this.grade = grade;
        this.tier = tier;
        this.componentType = componentType;
    }

}
