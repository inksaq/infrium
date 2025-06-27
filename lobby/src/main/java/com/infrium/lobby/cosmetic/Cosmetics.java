package com.infrium.lobby.cosmetic;

import lombok.Getter;

@Getter
public enum Cosmetics {
    MEDUSA_HEAD("Medusa Head", 1000, "Binds player in stone for 1(s)", false),
    PARTICLE_WINGS("Particle Wings", 500, "Gives player wings made of particles", true),
    HALO("Halo", 200, "Gives player a halo above their head", true),
    MIDAS_TOUCH("Midas Touch", 2000, "Everything players touch turns to gold", true),;

    private final String name;
    private final int cost;
    private final String description;
    private final boolean unlocked;

    Cosmetics(String name, int cost, String description, boolean unlocked) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.unlocked = unlocked;

    }

}
