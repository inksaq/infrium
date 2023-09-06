package ltd.bui.infrium.game.item;

import lombok.Getter;

public enum Grade {
    //Grade Multiplier effects Lifespan
    SHATTERED(-3, 31000000),
    FRAGMENTED(-2, 900),
    CHIPPED(-1,     1100),
    REFURBISHED(0, 1200),
    FACTORY(1,      2400),
    PRIMED(2,       3600),
    POLISHED(3,     4800),
    IMMACULATE(4, 5600),
    NEXA_CORE(5,    7200),
    SINGULARITY(6, 5.00),
    QUANTUM(7,      7.00);

    @Getter private int gradeLadder;
    @Getter private double lifespan;



    Grade(int gradeLadder, double lifespan) {
        this.gradeLadder = gradeLadder;
        this.lifespan = lifespan;
    }
}
