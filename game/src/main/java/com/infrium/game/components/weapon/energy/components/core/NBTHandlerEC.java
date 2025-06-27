package com.infrium.game.components.weapon.energy.components.core;

import de.tr7zw.changeme.nbtapi.iface.NBTHandler;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.jetbrains.annotations.NotNull;

public class NBTHandlerEC implements NBTHandler {
    @Override
    public void set(@NotNull ReadWriteNBT nbt, @NotNull String key, @NotNull Object value) {

    }

    @Override
    public Object get(@NotNull ReadableNBT nbt, @NotNull String key) {
        return null;
    }
}
