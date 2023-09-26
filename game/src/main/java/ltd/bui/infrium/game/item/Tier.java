package ltd.bui.infrium.game.item;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Tier {
    I(1, ChatColor.WHITE +  "I",30, 300,1, 38, 7, 1, 2),
    II(2,ChatColor.WHITE +"II",45, 750,1, 50, 9, 2, 3),
    III(3,ChatColor.WHITE + "III",60, 2450,2, 65, 13, 3, 4),
    IV(4,ChatColor.WHITE + "IV",75, 4500, 2,79, 18, 4, 5),
    V(5,ChatColor.WHITE + "V",120, 6850, 3,135, 22, 5, 6), //blueprint
    VI(6,ChatColor.WHITE + "VI",30, 300, 3, 132, 28, 6, 7),
    IX(9,ChatColor.WHITE + "IX",30, 300, 0, 38, 30, 10, 20),
    XV(15,ChatColor.WHITE + "XV",30, 300, 0, 38, 40, 20, 40),
    LCX(160,ChatColor.WHITE + "LCX",6666, 9999, 0, 7777, 55, 22, 222); // eldertech

    @Getter private final int ladder;
    @Getter private final String tierFormat;
    @Getter private final int rechargeRate;
    @Getter private final int capacitance;

    @Getter private final int gigahertzUpgradeRequirement;
    @Getter private final int energyOutputRate;
    @Getter private final int gigaHertz;
    @Getter private final int idleDraw;
    @Getter private final int heatRate;

    Tier(int ladder, String tierFormat,  int rechargeRate, int capacitance,int gigahertzUpgradeRequirement, int energyOutputRate, int gigaHertz, int idleDraw, int heatRate) {
        this.ladder = ladder;
        this.tierFormat = tierFormat;
        this.rechargeRate = rechargeRate;
        this.capacitance = capacitance;
        this.gigahertzUpgradeRequirement = gigahertzUpgradeRequirement;
        this.energyOutputRate = energyOutputRate;
        this.gigaHertz = gigaHertz;
        this.idleDraw = idleDraw;
        this.heatRate = heatRate;
    }
    public static Tier getTierLadder(int tierLadder) {
        for (Tier tier : Tier.values()) {
            if (tier.getLadder() == tierLadder) {
                return tier;
            }
        }
        return Tier.I;
    }
}
