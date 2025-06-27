package com.infrium.game.components.weapon.energy.components;

import com.infrium.core.item.ComplexItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class EnergyWeaponDataType implements ComplexItemBuilder.ItemDataType<EnergyWeaponState> {
    private final JavaPlugin plugin;

    public EnergyWeaponDataType(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void serialize(PersistentDataContainer container, NamespacedKey key, EnergyWeaponState state) {
        container.set(new NamespacedKey(plugin, key + ".energy"), PersistentDataType.DOUBLE, state.getCurrentEnergy());
        container.set(new NamespacedKey(plugin, key + ".maxEnergy"), PersistentDataType.DOUBLE, state.getMaxEnergy());
        container.set(new NamespacedKey(plugin, key + ".rechargeRate"), PersistentDataType.DOUBLE, state.getRechargeRate());
        container.set(new NamespacedKey(plugin, key + ".active"), PersistentDataType.BYTE, state.isActive() ? (byte)1 : (byte)0);
        container.set(new NamespacedKey(plugin, key + ".heat"), PersistentDataType.DOUBLE, state.getHeat());
        container.set(new NamespacedKey(plugin, key + ".lastUsed"), PersistentDataType.LONG, state.getLastUsedTime());
    }

    @Override
    public EnergyWeaponState deserialize(PersistentDataContainer container, NamespacedKey key) {
        return new EnergyWeaponState(
                container.getOrDefault(new NamespacedKey(plugin, key + ".energy"), PersistentDataType.DOUBLE, 0.0),
                container.getOrDefault(new NamespacedKey(plugin, key + ".maxEnergy"), PersistentDataType.DOUBLE, 100.0),
                container.getOrDefault(new NamespacedKey(plugin, key + ".rechargeRate"), PersistentDataType.DOUBLE, 1.0),
                container.getOrDefault(new NamespacedKey(plugin, key + ".active"), PersistentDataType.BYTE, (byte)0) == 1,
                container.getOrDefault(new NamespacedKey(plugin, key + ".heat"), PersistentDataType.DOUBLE, 0.0),
                container.getOrDefault(new NamespacedKey(plugin, key + ".lastUsed"), PersistentDataType.LONG, 0L)
        );
    }

    @Override
    public boolean isValid(EnergyWeaponState value) {
        return value != null &&
                value.getCurrentEnergy() >= 0 &&
                value.getMaxEnergy() > 0 &&
                value.getRechargeRate() >= 0 &&
                value.getHeat() >= 0;
    }

    @Override
    public Class<EnergyWeaponState> getDataClass() {
        return EnergyWeaponState.class;
    }
}

