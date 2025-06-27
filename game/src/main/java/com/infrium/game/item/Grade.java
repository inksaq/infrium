package com.infrium.game.item;

import lombok.Getter;

import org.bukkit.ChatColor;

public enum Grade {
    //Grade Multiplier effects Lifespan
    SHATTERED(-3,ChatColor.BLACK.toString() + "Shattered", 31000000,1),
    FRAGMENTED(-2,ChatColor.DARK_GRAY.toString() + "Fragmented", 41000000,1),
    CHIPPED(-1, ChatColor.GRAY.toString() + "Chipped",    46000000,1),
    REFURBISHED(0,ChatColor.BLUE.toString() + "Refurbished", 51000000, 1),
    FACTORY(1,ChatColor.WHITE.toString() + "Factory",      55000000,1),
    PRIMED(2,ChatColor.GREEN.toString() + "Prime",      59000000,1),
    POLISHED(3,ChatColor.AQUA.toString() + "Polished",     65000000,1),
    PRISTINE(4,ChatColor.DARK_AQUA.toString() + "Pristine", 71000000,1),
    REFORGED(5,ChatColor.LIGHT_PURPLE.toString() + "Reforged",    76000000,0),
    BLACK_HOLE(6,ChatColor.DARK_PURPLE.toString() + "o", 81000000,0),
    STAR_FORGED(7, ChatColor.YELLOW.toString() + "*",     99000000,0);

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
