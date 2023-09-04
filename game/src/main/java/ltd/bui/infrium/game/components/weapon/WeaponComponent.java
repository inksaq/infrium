package ltd.bui.infrium.game.components.weapon;

import ltd.bui.infrium.core.api.components.Component;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.testing.commands.TestCommand;
import ltd.bui.infrium.game.components.testing.listeners.TestListener;
import ltd.bui.infrium.game.components.weapon.gun.GunRegistry;
import ltd.bui.infrium.game.components.weapon.gun.WeaponListener;
import ltd.bui.infrium.game.components.weapon.gun.armory.SCAR90;
import ltd.bui.infrium.game.components.weapon.gun.commands.GetWeaponCC;

public class WeaponComponent extends Component<Settlements> {
    private WeaponComponent instance;
    private final SCAR90 scar90; // Your gun


    public WeaponComponent() {
        instance = this;
        scar90 = new SCAR90("scar90");
//        WeaponRegistry.registerWeapons();
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

