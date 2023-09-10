package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public abstract class ComponentUpgrade<T extends CoreComponent>{
    @Getter @Setter
    protected Rarity rarity;
    @Getter @Setter
    protected Grade grade;
    @Getter @Setter
    protected Tier tier;

    @Getter
    private T appliedTo;

    @Getter @Setter
    protected ComponentUpgradeType componentUpgradeType;

    public ComponentUpgrade(Rarity rarity, Grade grade, Tier tier, ComponentUpgradeType componentUpgradeType) {
        this.rarity = rarity;
        this.grade = grade;
        this.tier = tier;
        this.componentUpgradeType = componentUpgradeType;
    }

    public void setAppliedTo(T component) {
        this.appliedTo = component;
    }

    public void unSetAppliedTo() {
        this.appliedTo = null;
    }
}