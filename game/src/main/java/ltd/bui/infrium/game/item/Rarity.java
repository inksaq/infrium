package ltd.bui.infrium.game.item;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Rarity {
    MASS(-3, ChatColor.WHITE + "", 1.01, 1.01,1.00, 1.05 ,0.35, 2), //Mass Manufactured / GREY
    PRIME(-2,ChatColor.GREEN + "*",  1.04, 1.04,1.01,1.11, 0.60, 4), //Prime Prototype / GREEN
    RARE(-1, ChatColor.AQUA + "**", 1.08, 1.08, 1.02,1.15,0.85, 5), //Rare Relic / AQUA
    EPIC(0,ChatColor.LIGHT_PURPLE + "***", 1.10, 1.10,1.03, 1.20,0.95, 6), // Epic Eclipse / LIGHT PURPLE
    LEGENDARY(1,ChatColor.YELLOW + "****", 1.14, 1.14,1.04,1.35, 1.15, 8), // Legendary Lumen / YELLOW
    CONTRABAND(2,ChatColor.DARK_RED + "*****", 1.20, 1.20,1.05,1.50, 1.45, 10), // Contraband Conquest / RED
    BLACKMARKET(3,ChatColor.BLACK + "******", 1.30, 1.30,1.06,1.95, 1.75, 15), // Blackmarket Blood / DARK_GRAY
    PHOTONIC(4,ChatColor.GOLD + "#", 1.55, 1.55,1.02,1.97, 2.25, 8), //Taboo Titan / DARK_PURPLE
    DIMENSIONAL(5,ChatColor.DARK_AQUA + "##", 1.80, 1.80,1.02,1.99, 3.00, 10), //Forbidden Phantom / DARK_AQUA
    DARK_MATTER(6,ChatColor.DARK_PURPLE + "###", 1.99, 1.99,1.02,2.50, 5.00, 12); //Oblivion Order / BLACK

    @Getter private final int ladder;
    @Getter private final String rarityFormat;
    @Getter private final double thresholdMultiplier;
    @Getter private final double outputRateMultiplier;
    @Getter private final double chargeRateMultiplier;

    @Getter private final double capacitanceMultiplier;
    @Getter private final double lifespanMultiplier;
    @Getter private final int componentUpgradeLimit;

    Rarity(int ladder, String rarityFormat, double thresholdMultiplier, double outputRateMultiplier, double chargeRateMultiplier, double capacitanceMultiplier, double lifespanMultiplier, int componentUpgradeLimit) {
        this.ladder = ladder;
        this.rarityFormat = rarityFormat;
        this.thresholdMultiplier = thresholdMultiplier;
        this.outputRateMultiplier = outputRateMultiplier;
        this.chargeRateMultiplier = chargeRateMultiplier;
        this.capacitanceMultiplier = capacitanceMultiplier;
        this.lifespanMultiplier = lifespanMultiplier;
        this.componentUpgradeLimit = componentUpgradeLimit;
    }

    public static Rarity getRarityLadder(int rarityLadder) {
        for (Rarity rarity : Rarity.values()) {
            if (rarity.getLadder() == rarityLadder) {
                return rarity;
            }
        }
        return Rarity.MASS;
    }
}
