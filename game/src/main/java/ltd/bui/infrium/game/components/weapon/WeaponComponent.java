package ltd.bui.infrium.game.components.weapon;

import lombok.Getter;
import ltd.bui.infrium.core.api.components.Component;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.testing.commands.TestCommand;
import ltd.bui.infrium.game.components.testing.listeners.TestListener;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.gun.GunRegistry;
import ltd.bui.infrium.game.components.weapon.gun.WeaponListener;
import ltd.bui.infrium.game.components.weapon.gun.armory.SCAR90;
import ltd.bui.infrium.game.components.weapon.gun.commands.GetWeaponCC;

import java.util.*;

public class WeaponComponent extends Component<Settlements> {
    private static WeaponComponent instance;
    private final SCAR90 scar90; // Your gun

    @Getter
    private final HashMap<UUID, FrameBody> framebodies;


    public WeaponComponent() {
        instance = this;
        framebodies = new HashMap<>();
        scar90 = new SCAR90("scar90");
//        WeaponRegistry.registerWeapons();
    }

    public FrameBody getFrameBody(int numberStored) {
        if (numberStored <= 0 || numberStored > framebodies.size()) {
            throw new IllegalArgumentException("Invalid numberStored value.");
        }
        List<Map.Entry<UUID, FrameBody>> entries = new ArrayList<>(framebodies.entrySet());
        return entries.get(numberStored - 1).getValue();
    }

    public void setFrameBody(UUID key, FrameBody frameBody) {
        framebodies.put(key, frameBody);
    }


    public static WeaponComponent getInstance() {
        return instance;
    }

    @Override
    public void enable(Settlements plugin) {
        if (instance == null) instance = new WeaponComponent();

        GunRegistry.registerGun(scar90.getGunItem(), scar90, scar90.getName());

    }

    @Override
    public void disable(Settlements plugin) {

    }

    @Override
    public void registerListener(Settlements plugin) {
        new TestListener(plugin);
        new WeaponListener(plugin);
    }

    @Override
    public void registerCommands(Settlements plugin) {
        new TestCommand();
        new GetWeaponCC();
    }


}

