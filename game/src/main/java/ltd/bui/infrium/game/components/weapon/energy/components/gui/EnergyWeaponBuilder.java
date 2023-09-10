package ltd.bui.infrium.game.components.weapon.energy.components.gui;

import ltd.bui.infrium.core.item.ItemBuilder;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.ChargeCell;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.CoreProcessor;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.LensConduit;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class EnergyWeaponBuilder {

    private EnergyCore energyCore;
    private CoreProcessor coreProcessor;
    private ChargeCell chargeCell;
    private LensConduit lensConduit;

    EnergyWeaponBuilder() {
        this.size = 1;
        this.name = null;
        this.lore = null;
        this.material = Material.AIR;
        this.unbreakable = false;
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

    public EnergyWeaponBuilder recompute() {
        // Update attributes based on EnergyCore, ChargeCell, etc.
        // E.g., updating lore with new stats

        // Just a simple example:
        if (energyCore != null) {
            this.lore.add(Component.text("Energy: " + energyCore.getEnergy()));
        }
        if (chargeCell != null) {
            this.lore.add(Component.text("Charge: " + chargeCell.getCharge()));
        }
        // ... and so on for other components

        return this;
    }

    private int size;
    private Component name;
    private List<Component> lore;
    private Material material;
    private boolean unbreakable;


    public static EnergyWeaponBuilder builder() {
        return new EnergyWeaponBuilder();
    }

    public int getSize() {
        return size;
    }

    public EnergyWeaponBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public Component getName() {
        return name;
    }

    public EnergyWeaponBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public List<Component> getLore() {
        return lore;
    }

    public EnergyWeaponBuilder setLore(List<Component> lore) {
        this.lore = lore;
        return this;
    }


    public Material getMaterial() {
        return material;
    }

    public EnergyWeaponBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public EnergyWeaponBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStack build(boolean glow) {
        ItemStack itemStack = new ItemStack(material, size);
        var meta = itemStack.getItemMeta();

        meta.displayName(this.name);
        meta.lore(this.lore);
        meta.setUnbreakable(unbreakable);
        meta.getPersistentDataContainer().set(NamespacedKey.randomKey(), PersistentDataType.);
        itemStack.getItemMeta().getPersistentDataContainer()
        if (glow) {
            meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
