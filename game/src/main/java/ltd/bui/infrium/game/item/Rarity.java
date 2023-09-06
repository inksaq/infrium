package ltd.bui.infrium.game.item;

import lombok.Getter;

public enum Rarity {
    MASS(-3, 1.01, 1.01, 1.05 ,0.35, 2), //Mass Manufactured / GREY
    PRIME(-2, 1.04, 1.04,1.11, 0.60, 4), //Prime Prototype / GREEN
    RARE(-1, 1.08, 1.08, 1.15,0.85, 5), //Rare Relic / AQUA
    EPIC(0, 1.10, 1.10, 1.20,0.95, 6), // Epic Eclipse / LIGHT PURPLE
    LEGENDARY(1, 1.14, 1.14,1.35, 1.15, 8), // Legendary Lumen / YELLOW
    CONTRABAND(2, 1.20, 1.20,1.50, 1.45, 10), // Contraband Conquest / RED
    BLACKMARKET(3, 1.30, 1.30,1.95, 1.75, 15), // Blackmarket Blood / DARK_GRAY
    TITAN(4, 1.55, 1.55,1.97, 2.25, 8), //Taboo Titan / DARK_PURPLE
    PHANTOM(5, 1.80, 1.80,1.99, 3.00, 10), //Forbidden Phantom / DARK_AQUA
    OBLIVION(6, 1.99, 1.99,2.50, 5.00, 12); //Oblivion Order / BLACK

    @Getter private final int ladder;
    @Getter private final double thresholdMultiplier;
    @Getter private final double outputRateMultiplier;

    @Getter private final double capacitanceMultiplier;
    @Getter private final double lifespanMultiplier;
    @Getter private final int componentUpgradeLimit;

    Rarity(int ladder, double thresholdMultiplier, double outputRateMultiplier, double capacitanceMultiplier, double lifespanMultiplier, int componentUpgradeLimit) {
        this.ladder = ladder;
        this.thresholdMultiplier = thresholdMultiplier;
        this.outputRateMultiplier = outputRateMultiplier;
        this.capacitanceMultiplier = capacitanceMultiplier;
        this.lifespanMultiplier = lifespanMultiplier;
        this.componentUpgradeLimit = componentUpgradeLimit;
    }
}
