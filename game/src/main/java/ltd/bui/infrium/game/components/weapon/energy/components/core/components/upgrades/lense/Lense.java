package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.lense;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class Lense implements ILense {

    @Getter
    @Setter
    protected Rarity rarity;
    @Getter @Setter
    protected Tier tier;
    @Getter @Setter
    protected LenseType lenseType;
    @Getter @Setter
    protected LenseState lenseState;

    public Lense(Rarity rarity, Tier tier, LenseType lenseType, LenseState lenseState) {
        this.rarity = rarity;
        this.tier = tier;
        this.lenseType = lenseType;
        this.lenseState = lenseState;
    }
}
