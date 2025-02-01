package ltd.bui.infrium.game.components.weapon.workstation;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class Workstation {
    protected Map<Integer, ItemStack> slots;

    public abstract void replaceComponent(int slot, ItemStack newComponent);
    public abstract void removeComponent(int slot);
    public abstract void assembleItem();
}

