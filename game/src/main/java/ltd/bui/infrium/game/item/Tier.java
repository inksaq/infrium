package ltd.bui.infrium.game.item;

import lombok.Getter;

public enum Tier {
    I(1,30, 300,1, 38, 7, 1, 2),
    II(2,45, 750,1, 50, 9, 2, 3),
    III(3,60, 2450,2, 65, 13, 3, 4),
    IV(4,75, 4500, 2,79, 18, 4, 5),
    V(5,120, 6850, 3,135, 22, 5, 6), //blueprint
    VI(6,30, 300, 3, 132, 28, 6, 7),
    IX(9,30, 300, 0, 38, 30, 10, 20),
    XV(15,30, 300, 0, 38, 40, 20, 40),
    LCX(160,6666, 9999, 0, 7777, 55, 22, 222); // eldertech

    @Getter private final int ladder;
    @Getter private final int rechargeRate;
    @Getter private final int capacitance;

    @Getter private final int gigahertzUpgradeRequirement;
    @Getter private final int energyOutputRate;
    @Getter private final int gigaHertz;
    @Getter private final int idleDraw;
    @Getter private final int heatRate;

    Tier(int ladder,int rechargeRate, int capacitance,int gigahertzUpgradeRequirement, int energyOutputRate, int gigaHertz, int idleDraw, int heatRate) {
        this.ladder = ladder;
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
