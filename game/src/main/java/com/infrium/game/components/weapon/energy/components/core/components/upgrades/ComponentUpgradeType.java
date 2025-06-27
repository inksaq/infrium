package com.infrium.game.components.weapon.energy.components.core.components.upgrades;

import lombok.Getter;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentType;

public enum ComponentUpgradeType {
    EMPTY(null,0),
    OVERCHARGE(CoreComponentType.CHARGE_CELL, 20),
    OVERCLOCK(CoreComponentType.CORE_PROCESSOR, 20),
    OVERLOAD(CoreComponentType.ENERGY_CORE, 20),
    OVERVOLT(CoreComponentType.CHARGE_CELL, 20),
    UNDERCLOCK(CoreComponentType.CORE_PROCESSOR, 20),
    UNDERLOAD(CoreComponentType.ENERGY_CORE, 20),
    UNDERVOLT(CoreComponentType.CHARGE_CELL, 20),
    SUPERCLOCK(CoreComponentType.CORE_PROCESSOR, 60),
    SUPERLOAD(CoreComponentType.ENERGY_CORE, 60),
    SUPERVOLT(CoreComponentType.CHARGE_CELL, 60),
    FAST_CHARGE(CoreComponentType.CHARGE_CELL, 20),
    CONSTANT_CHARGE(CoreComponentType.CHARGE_CELL, 80);


    @Getter
    private CoreComponentType coreComponentType;
    @Getter
    private double heatRate;

    ComponentUpgradeType(CoreComponentType coreComponentType, double baseHeatRate) {
        this.coreComponentType = coreComponentType;
        this.heatRate = baseHeatRate;
    }
}
