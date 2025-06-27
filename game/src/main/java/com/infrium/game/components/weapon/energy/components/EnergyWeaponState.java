package com.infrium.game.components.weapon.energy.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class EnergyWeaponState {
    @Getter @Setter private double currentEnergy;
    @Getter @Setter private final double maxEnergy;
    @Getter @Setter private final double rechargeRate;
    @Getter @Setter private boolean active;
    @Getter @Setter private double heat;
    @Getter @Setter private long lastUsedTime;
    

    // Constructor and getters/setters...
}
