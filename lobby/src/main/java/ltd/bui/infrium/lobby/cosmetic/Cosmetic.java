package ltd.bui.infrium.lobby.cosmetic;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class Cosmetic {
    @Getter
    protected String name;

    protected boolean isActive;


    protected boolean isUnlocked;

    public Cosmetic(String name, boolean isUnlocked) {
        this.name = name;
        this.isActive = false;
        this.isUnlocked = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public void toggle(Player player) {
        if (isActive) {
            remove(player);
        } else {
            apply(player);
        }
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void unlock() {
        this.isUnlocked = true;
    }

    public abstract void apply(Player player);
    public abstract void remove(Player player);
}