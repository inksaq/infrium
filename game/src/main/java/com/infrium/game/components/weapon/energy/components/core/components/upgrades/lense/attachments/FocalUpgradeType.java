package com.infrium.game.components.weapon.energy.components.core.components.upgrades.lense.attachments;

import lombok.Getter;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentType;

public enum FocalUpgradeType {
    EMPTY(null,0),
    SCATTER(CoreComponentType.LENS_CONDUIT, 20);


    @Getter
    private CoreComponentType coreComponentType;
    @Getter
    private double heatRate;

    FocalUpgradeType(CoreComponentType coreComponentType, double baseHeatRate) {
        this.coreComponentType = coreComponentType;
        this.heatRate = baseHeatRate;
    }
}
