package ltd.bui.infrium.game.item;

import lombok.Getter;

public enum Grade {
    //Grade Multiplier effects Lifespan
    SHATTERED(-3, 31000000,1.8),
    FRAGMENTED(-2, 61000000,1.40),
    CHIPPED(-1,     1100,1.38),
    REFURBISHED(0, 1200, 1.25),
    FACTORY(1,      2400,1.15),
    PRIMED(2,       3600,1.05),
    POLISHED(3,     48000.79,1.01),
    IMMACULATE(4, 56000.75,1.00),
    NEXA_CORE(5,    7200,0.95),
    SINGULARITY(6, 5.00,0.5),
    QUANTUM(7,      7.00,0.10);

    @Getter private int gradeLadder;
    @Getter private int lifespan;
    @Getter private double stabilityMultiplier;



    Grade(int gradeLadder, double lifespan, double stabilityMultiplier) {
        this.gradeLadder = gradeLadder;
        this.lifespan = lifespan;
        this.stabilityMultiplier = stabilityMultiplier;
    }

    public static Grade getGradeLadder(int gradeLadder) {
        for (Grade grade : Grade.values()) {
            if (grade.gradeLadder == gradeLadder) {
                return grade;
            }
        }
        return Grade.SHATTERED;
    }
}
