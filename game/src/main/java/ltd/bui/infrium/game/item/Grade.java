package ltd.bui.infrium.game.item;

import lombok.Getter;

public enum Grade {
    //Grade Multiplier effects Lifespan
    SHATTERED(-3, 31000000,1),
    FRAGMENTED(-2, 41000000,1),
    CHIPPED(-1,     46000000,1),
    REFURBISHED(0, 51000000, 1),
    FACTORY(1,      55000000,1),
    PRIMED(2,       59000000,1),
    POLISHED(3,     65000000,1),
    IMMACULATE(4, 71000000,1),
    NEXA_CORE(5,    76000000,0),
    SINGULARITY(6, 81000000,0),
    QUANTUM(7,      99000000,0);

    @Getter private int gradeLadder;
    @Getter private int lifespan;
    @Getter private int stabilityMultiplier;



    Grade(int gradeLadder, int lifespan, int stabilityMultiplier) {
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
