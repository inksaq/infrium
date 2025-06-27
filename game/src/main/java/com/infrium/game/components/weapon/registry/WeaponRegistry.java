package com.infrium.game.components.weapon.registry;

import de.tr7zw.changeme.nbtapi.NBT;
import lombok.Getter;
import com.infrium.game.components.weapon.energy.components.core.components.FrameBody;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class WeaponRegistry {

    @Getter
    public static WeaponRegistry instance;

    private final HashMap<UUID, FrameBody> framebodies;


    //todo register framebodies on load from db inventories

    public WeaponRegistry() {
        instance = this;
        framebodies = new HashMap<>();



    }

    public boolean isFrameBody(ItemStack itemStack) {
        if (!NBT.readNbt(itemStack).hasTag("uuid")) return false;
        else return true;
    }

    public FrameBody getFrameBody(ItemStack itemStack) {
        String uuid = NBT.readNbt(itemStack).getString("uuid");
        return framebodies.get(UUID.fromString(uuid));

    }

    public boolean updateWeapon(FrameBody frameBody) {
        if (frameBody == null) return false;
        if (!getFramebodies().containsKey(frameBody.getFrameUUID())) return false;

        try {
            FrameBody oldReg = framebodies.get(frameBody.getFrameUUID());
            framebodies.put(oldReg.getFrameUUID(), frameBody);
            return true;
        } catch (Exception e) {
            return false;
        }
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
