package ltd.bui.infrium.game.components.weapon.energy.components.gui;

import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FrameBodyDataType implements PersistentDataType<PersistentDataContainer, FrameBody> {

    public FrameBodyDataType() {
    }

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<FrameBody> getComplexType() {
        return FrameBody.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull FrameBody complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer persistentDataContainer = context.newPersistentDataContainer();



        return persistentDataContainer;
    }

    @Override
    public @NotNull FrameBody fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        FrameBody frameBody = new FrameBody();
        frameBody.setEnergyCore(primitive.get(""));
        return null;
    }

    private NamespacedKey key(String key) {
        return new NamespacedKey(javaPlugin, key);
    }

    private int getOrDefault(Integer integer, int defaultValue) {
        return integer != null ? integer : defaultValue;
    }

    private boolean fromByte(Byte byteValue, boolean defaultValue) {
        return byteValue != null ? byteValue == 1 : defaultValue;
    }
}
