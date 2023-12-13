package ltd.bui.infrium.game.components.weapon;

import lombok.Getter;
import ltd.bui.infrium.core.api.components.Component;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.testing.commands.TestCommand;
import ltd.bui.infrium.game.components.testing.listeners.TestListener;
import ltd.bui.infrium.game.components.weapon.energy.components.gui.EnergyCoreDataType;
import ltd.bui.infrium.game.components.weapon.energy.components.gui.FrameBodyDataType;
import ltd.bui.infrium.game.components.weapon.gun.GunRegistry;
import ltd.bui.infrium.game.components.weapon.gun.WeaponListener;
import ltd.bui.infrium.game.components.weapon.gun.armory.SCAR90;
import ltd.bui.infrium.game.components.weapon.gun.commands.GetWeaponCC;
import ltd.bui.infrium.game.components.weapon.registry.WeaponRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class WeaponComponent extends Component<Settlements> {
    private static WeaponComponent instance;
    private final SCAR90 scar90; // Your gun

    private JavaPlugin plugin = Settlements.getInstance();

    @Getter
    private final FrameBodyDataType frameBodyDataType;
    @Getter
    private final EnergyCoreDataType energyCoreDataType;

    @Getter
    private final NamespacedKey weaponKey;

    @Getter
    private WeaponRegistry weaponRegistry;


    public WeaponComponent() {
        instance = this;
        weaponRegistry = new WeaponRegistry();
        this.weaponKey = new NamespacedKey(Settlements.getInstance(), "ergwp");
        this.frameBodyDataType = new FrameBodyDataType(plugin);
        this.energyCoreDataType = new EnergyCoreDataType(plugin);
        scar90 = new SCAR90("scar90");
//        WeaponRegistry.registerWeapons();
    }


//    public FrameBody getItemFrameBody(ItemStack itemStack) {
//        var fbNBT = NBT.itemStackToNBT(itemStack);
////        fbNBT.
//        return
//    }



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

