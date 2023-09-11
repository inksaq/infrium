package ltd.bui.infrium.game.components.weapon.energy.components.gui;

import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class EnergyCoreDataType implements PersistentDataType<PersistentDataContainer, EnergyCore> {


    private JavaPlugin plugin;

    public EnergyCoreDataType(JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
    }

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<EnergyCore> getComplexType() {
        return EnergyCore.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull EnergyCore energyCore, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer persistentDataContainer = context.newPersistentDataContainer();
        persistentDataContainer.set(key("uuid"), Settlements.uuidTagType, energyCore.getUuid());
        persistentDataContainer.set(key("tier"), INTEGER, energyCore.getTier().getLadder());
        persistentDataContainer.set(key("grade"), INTEGER, energyCore.getGrade().getGradeLadder());
        persistentDataContainer.set(key("rarity"), INTEGER, energyCore.getRarity().getLadder());
        persistentDataContainer.set(key("capacitance"), INTEGER, energyCore.getCoreEnergyCapacitance());
        persistentDataContainer.set(key("idleDrawRate"), INTEGER, energyCore.getIdleDrawRate());
        persistentDataContainer.set(key("rechargeRate"), INTEGER, energyCore.getRechargeRate());
        persistentDataContainer.set(key("outputRate"), INTEGER, energyCore.getOutputEnergyRate());
        persistentDataContainer.set(key("heatRate"), INTEGER, energyCore.getHeatRate());
//        writeAttachments(persistentDataContainer, weaponData.getAttachments());
        return persistentDataContainer;
    }

    @NotNull
    @Override
    public EnergyCore fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        var rarity = primitive.get(key("rarity"), INTEGER);
        var grade = primitive.get(key("grade"), INTEGER);
        var tier = primitive.get(key("tier"), INTEGER);
        EnergyCore energyCore = new EnergyCore(Rarity.getRarityLadder(rarity), Grade.getGradeLadder(grade), Tier.getTierLadder(tier));
        energyCore.setUuid(primitive.get(key("uuid"), Settlements.uuidTagType));
        energyCore.setCoreEnergyCapacitance(primitive.get(key("capacitance"), INTEGER));
        energyCore.setIdleDrawRate(primitive.get(key("idleDrawRate"), INTEGER));
        energyCore.setRechargeRate(primitive.get(key("rechargeRate"), INTEGER));
        energyCore.setOutputEnergyRate(primitive.get(key("outputRate"), INTEGER));
        energyCore.setHeatRate(primitive.get(key("heatRate"), INTEGER));
        return energyCore;
    }


    private NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }

    private int getOrDefault(Integer integer, int defaultValue) {
        return integer != null ? integer : defaultValue;
    }

    private boolean fromByte(Byte byteValue, boolean defaultValue) {
        return byteValue != null ? byteValue == 1 : defaultValue;
    }
}
