package ltd.bui.infrium.game.components.weapon.energy.components;

import de.tr7zw.changeme.nbtapi.iface.NBTHandler;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.FrameBody;
import org.jetbrains.annotations.NotNull;

public abstract class FrameBodyNBTHandler implements NBTHandler<FrameBody> {

    @Override
    public void set(@NotNull ReadWriteNBT nbt, @NotNull String key, @NotNull FrameBody value) {
        nbt.removeKey(key); // Removing any existing key
        nbt.getOrCreateCompound(key).mergeCompound(value.serializeToNBT()); // Storing serialized data
    }

    @Override
    public FrameBody get(@NotNull ReadWriteNBT nbt, @NotNull String key) {
        if (nbt.get(key, Fra)) { // Check if the key exists
            ReadableNBT frameBodyNBT = nbt.getCompound(key); // Getting the compound associated with the key
            FrameBody fb = new FrameBody();
            fb.deserializeFromNBT(frameBodyNBT); // Assuming FrameBody has a method to populate itself from NBT
            return fb;
        }
        return null; // Or return a new default FrameBody, based on your requirements
    }
}
