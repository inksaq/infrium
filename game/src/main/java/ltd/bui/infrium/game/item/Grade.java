package ltd.bui.infrium.game.item;

import lombok.Getter;

import org.bukkit.ChatColor;

public enum Grade {
    //Grade Multiplier effects Lifespan
    SHATTERED(-3,ChatColor.BLACK.toString() + "-", 31000000,1),
    FRAGMENTED(-2,ChatColor.DARK_GRAY.toString() + "-", 41000000,1),
    CHIPPED(-1, ChatColor.GRAY.toString() + "-",    46000000,1),
    REFURBISHED(0,ChatColor.BLUE.toString() + "=", 51000000, 1),
    FACTORY(1,ChatColor.WHITE.toString() + "+",      55000000,1),
    PRIMED(2,ChatColor.LIGHT_PURPLE.toString() + "+",      59000000,1),
    POLISHED(3,ChatColor.AQUA.toString() + "+",     65000000,1),
    IMMACULATE(4,ChatColor.YELLOW.toString() + "+", 71000000,1),
    NEXA_CORE(5,ChatColor.GOLD.toString() + "+",    76000000,0),
    SINGULARITY(6,ChatColor.DARK_AQUA.toString() + "+", 81000000,0),
    QUANTUM(7, ChatColor.DARK_PURPLE.toString() + "+",     99000000,0);

    @Getter private int gradeLadder;
    @Getter private String gradeFormat;
    @Getter private int lifespan;
    @Getter private int stabilityMultiplier;



    Grade(int gradeLadder, String gradeFormat, int lifespan, int stabilityMultiplier) {
        this.gradeLadder = gradeLadder;
        this.gradeFormat = gradeFormat;
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
