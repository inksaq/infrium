package ltd.bui.infrium.game.item;

import lombok.Getter;

public enum Tier {
    I(1,30.00, 300.00,1, 38.00, 7, 1, 2),
    II(2,45.00, 750.00,1, 50.00, 9, 2, 3),
    III(3,60.00, 2450.00,2, 65.00, 13, 3, 4),
    IV(4,75.00, 4500.00, 2,79.00, 18, 4, 5),
    V(5,120.00, 6850.00, 3,135.00, 22, 5, 6), //blueprint
    VI(6,30.00, 300.00, 3, 132.00, 28, 6, 7),
    IX(9,30.00, 300.00, 0, 38.00, 30, 10, 20),
    XV(15,30.00, 300.00, 0, 38.00, 40, 20, 40),
    LCX(160,6666.00, 9999.00, 0, 7777.00, 55, 22, 222); // eldertech

    @Getter private final int ladder;
    @Getter private final double rechargeRate;
    @Getter private final double capacitance;

    @Getter private final int gigahertzUpgradeRequirement;
    @Getter private final double energyOutputRate;
    @Getter private final double gigaHertz;
    @Getter private final int idleDraw;
    @Getter private final int heatRate;

    Tier(int ladder,double rechargeRate, double capacitance,int gigahertzUpgradeRequirement, double energyOutputRate, double gigaHertz, int idleDraw, int heatRate) {
        this.ladder = ladder;
        this.rechargeRate = rechargeRate;
        this.capacitance = capacitance;
        this.gigahertzUpgradeRequirement = gigahertzUpgradeRequirement;
        this.energyOutputRate = energyOutputRate;
        this.gigaHertz = gigaHertz;
        this.idleDraw = idleDraw;
        this.heatRate = heatRate;
    }
}
