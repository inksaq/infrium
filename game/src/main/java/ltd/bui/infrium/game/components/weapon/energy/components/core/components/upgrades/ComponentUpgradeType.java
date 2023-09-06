package ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades;

import lombok.Getter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;

public enum ComponentUpgradeType {
    OVERCHARGE(CoreComponentType.CHARGE_CELL, 20),
    OVERCLOCK(CoreComponentType.CORE_PROCESSOR, 20),
    OVERLOAD(CoreComponentType.ENERGY_CORE, 20),
    OVERVOLT(CoreComponentType.CHARGE_CELL, 20),
    UNDERCLOCK(CoreComponentType.CORE_PROCESSOR, 20),
    UNDERLOAD(CoreComponentType.ENERGY_CORE, 20),
    UNDERVOLT(CoreComponentType.CHARGE_CELL, 20),
    SUPERCLOCK(CoreComponentType.CORE_PROCESSOR, 60),
    SUPERLOAD(CoreComponentType.ENERGY_CORE, 60),
    SUPERVOLT(CoreComponentType.CHARGE_CELL, 60);

    @Getter
    private CoreComponentType coreComponentType;
    @Getter
    private double heatRate;

    ComponentUpgradeType(CoreComponentType coreComponentType, double baseHeatRate) {
        this.coreComponentType = coreComponentType;
        this.heatRate = baseHeatRate;
    }
}
