package ltd.bui.infrium.game.components.weapon.registry;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import lombok.Getter;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@Getter
public class WeaponRegistry {

    private final HashMap<UUID, FrameBody> framebodies;


    //todo register framebodies on load from db inventories

    public WeaponRegistry() {
        framebodies = new HashMap<>();



    }

    public FrameBody getFrameBody(ItemStack itemStack) {
        String uuid = NBT.readNbt(itemStack).getString("uuid");
        return framebodies.getOrDefault(UUID.fromString(uuid), null);

    }


    public boolean registerWeapon(FrameBody frameBody) {
        if (getFramebodies().containsKey(frameBody.getFrameUUID())) {
            System.out.println("already registered: " + frameBody.getFrameUUID() + " * " + getFramebodies().get(frameBody.getFrameUUID()).getFrameUUID());
            return false;
        }

        framebodies.put(frameBody.getFrameUUID(), frameBody);
        System.out.println("registered new framebody: " + frameBody.getFrameUUID());

        return false;
    }
}
