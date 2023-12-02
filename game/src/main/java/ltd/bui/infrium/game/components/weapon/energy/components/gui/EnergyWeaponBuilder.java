package ltd.bui.infrium.game.components.weapon.energy.components.gui;

import lombok.Getter;
import ltd.bui.infrium.game.components.weapon.WeaponComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.ChargeCell;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.CoreProcessor;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.LensConduit;
import ltd.bui.infrium.game.item.Grade;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnergyWeaponBuilder {

    private FrameBody frameBody;

    private EnergyCore energyCore;
    private CoreProcessor coreProcessor;
    private ChargeCell chargeCell;
    private LensConduit lensConduit;

    private boolean active;

    EnergyWeaponBuilder(Grade grade) {
        this.size = 1;
        this.frameBody = new FrameBody(grade);
        this.active = false;
        this.name = null;
        this.lore = null;
        this.material = Material.DIAMOND_HOE;
        this.unbreakable = true;
    }

    public EnergyWeaponBuilder setFrameBody(FrameBody frameBody) {
        this.frameBody = frameBody;
        return this;
    }

    public EnergyWeaponBuilder setEnergyCore(EnergyCore energyCore) {
        this.energyCore = energyCore;
        return this;
    }
    public EnergyWeaponBuilder setCoreProcessor(CoreProcessor coreProcessor) {
        this.coreProcessor = coreProcessor;
        return this;
    }
    public EnergyWeaponBuilder setChargeCell(ChargeCell chargeCell) {
        this.chargeCell = chargeCell;
        return this;
    }
    public EnergyWeaponBuilder setLensConduit(LensConduit lensConduit) {
        this.lensConduit = lensConduit;
        return this;
    }

    public void setActive() {
        if (chargeCell != null && coreProcessor != null && energyCore != null) {
            active = true;
        } else {
            active = false;
        }
    }

    public EnergyWeaponBuilder recompute() {
        // Update attributes based on EnergyCore, ChargeCell, etc.
        // E.g., updating lore with new stats

        // Just a simple example:
        if (energyCore != null) {
            this.lore.add(Component.text("Energy: " + energyCore.getCoreEnergyCapacitance()));
            this.lore.add(Component.text("OutputRate: " + energyCore.getOutputEnergyRate()));
        }
        if (chargeCell != null) {
            this.lore.add(Component.text("Charge: " + chargeCell.getCapacity()));
        }
        // ... and so on for other components

        return this;
    }

    @Getter
    private int size;
    @Getter
    private Component name;
    @Getter
    private List<Component> lore;
    @Getter
    private Material material;
    @Getter
    private boolean unbreakable;


    public static EnergyWeaponBuilder builder(Grade grade) {
        return new EnergyWeaponBuilder(grade);
    }

    public EnergyWeaponBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public EnergyWeaponBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public EnergyWeaponBuilder setLore(List<Component> lore) {
        this.lore = lore;
        return this;
    }


    public EnergyWeaponBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public EnergyWeaponBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStack build(boolean glow) {
        ItemStack itemStack = new ItemStack(material, size);
        var meta = itemStack.getItemMeta();
        this.recompute();
        meta.displayName(this.name);
        meta.lore(this.lore);
        meta.setUnbreakable(unbreakable);
        meta.getPersistentDataContainer().set(WeaponComponent.getInstance().getWeaponKey(), WeaponComponent.getInstance().getFrameBodyDataType(), frameBody);
        if (glow) {
            meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
