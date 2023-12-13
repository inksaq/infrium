package ltd.bui.infrium.game.components.weapon.energy.components.gui;

import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.weapon.WeaponComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.FrameBody;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FrameBodyDataType implements PersistentDataType<PersistentDataContainer, FrameBody> {

    private JavaPlugin javaPlugin;

    public FrameBodyDataType(JavaPlugin plugin) {
        this.javaPlugin = plugin;
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
    public @NotNull PersistentDataContainer toPrimitive(@NotNull FrameBody frameBody, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer persistentDataContainer = context.newPersistentDataContainer();
        persistentDataContainer.set(key("uuid"), Settlements.uuidTagType, frameBody.getFrameUUID());
        persistentDataContainer.set(key("grade"), INTEGER, frameBody.getGrade().getGradeLadder());
        persistentDataContainer.set(key("rarity"), INTEGER, frameBody.getLifespan());
        if (frameBody.getEnergyCore() != null) {
            persistentDataContainer.set(key("energyCore"), WeaponComponent.getInstance().getEnergyCoreDataType(), frameBody.getEnergyCore());
        }
//        if (frameBody.getCoreProcessor() != null) {
//            persistentDataContainer.set(key("coreProcessor"), WeaponComponent.getInstance().getEnergyCoreDataType(), frameBody.getCoreProcessor());
//        }
//        if (frameBody.getChargeCell() != null) {
//            persistentDataContainer.set(key("chargeCell"), WeaponComponent.getInstance().getEnergyCoreDataType(), frameBody.getChargeCell());
//        }
//        if (frameBody.getLensConduit() != null) {
//            persistentDataContainer.set(key("lensConduit"), WeaponComponent.getInstance().getEnergyCoreDataType(), frameBody.getLensConduit());
//        }
        persistentDataContainer.set(key("maxFrameAttachments"), PersistentDataType.INTEGER, frameBody.getMaxFrameAttachments());
//        writeAttachments(persistentDataContainer, weaponData.getAttachments());
        return persistentDataContainer;
    }

    @NotNull
    @Override
    public FrameBody fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        var grade = primitive.get(key("grade"), INTEGER);
        var rarity = primitive.get(key("rarity"), INTEGER);
        var tier = primitive.get(key("tier"), INTEGER);
        FrameBody frameBody = new FrameBody(Grade.getGradeLadder(grade), Rarity.getRarityLadder(rarity), Tier.getTierLadder(tier));
        frameBody.setFrameUUID(primitive.get(key("uuid"), Settlements.uuidTagType));
        frameBody.setMaxFrameAttachments(primitive.get(key("maxFrameAttachments"), INTEGER));
        if (primitive.get(key("energyCore"), WeaponComponent.getInstance().getEnergyCoreDataType()) != null) {
            frameBody.setEnergyCore(primitive.get(key("energyCore"), WeaponComponent.getInstance().getEnergyCoreDataType()));
        }

        return frameBody;
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
