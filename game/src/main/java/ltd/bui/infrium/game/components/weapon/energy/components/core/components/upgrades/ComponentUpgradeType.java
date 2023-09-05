package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades;

import lombok.Getter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;

public enum ComponentUpgradeType {
    OVERCHARGE(CoreComponentType.CHARGE_CELL),
    OVERCLOCK(CoreComponentType.CORE_PROCESSOR),
    OVERLOAD(CoreComponentType.ENERGY_CORE),
    OVERVOLT(CoreComponentType.CHARGE_CELL),
    UNDERCLOCK(CoreComponentType.CORE_PROCESSOR),
    UNDERLOAD(CoreComponentType.ENERGY_CORE),
    UNDERVOLT(CoreComponentType.CHARGE_CELL),
    SUPERCLOCK(CoreComponentType.CORE_PROCESSOR),
    SUPERLOAD(CoreComponentType.ENERGY_CORE),
    SUPERVOLT(CoreComponentType.CHARGE_CELL);

    @Getter
    private CoreComponentType coreComponentType;

    ComponentUpgradeType(CoreComponentType coreComponentType) {
        this.coreComponentType = coreComponentType;
    }
}
